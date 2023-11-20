package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.category.CategoryExistsException;
import ru.practicum.ewm.error.exception.event.EventNotExistException;
import ru.practicum.ewm.error.exception.event.EventPublishException;
import ru.practicum.ewm.error.exception.event.EventStateException;
import ru.practicum.ewm.error.exception.event.EventTimeException;
import ru.practicum.ewm.error.exception.user.UserAccessException;
import ru.practicum.ewm.error.exception.user.UserExistException;
import ru.practicum.ewm.error.exception.util.EntityExistException;
import ru.practicum.ewm.error.exception.util.TimeIntervalException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public EventDto save(Long userId, EventRequestDto dto) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User with id=" + userId + " does`t exist.");
                    throw new EntityExistException("User with id=" + userId + " does`t exist.");
                });

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> {
                    log.error("Category does`t exist.");
                    throw new EntityExistException("Category does`t exist.");
                });

        LocalDateTime eventDate = dto.getEventDate();

        if (eventDate == null || eventDate.isBefore(LocalDateTime.now().plusHours(2))) {

            log.error("Event date should be in future.");

            throw new TimeIntervalException("Event date should be in future.");
        }

        Event event = mapper.toEvent(dto);

        event.setCategory(category);

        event.setInitiator(user);

        return mapper.toDto(repository.save(event));
    }

    @Override
    public List<ShortEvenDto> get(Long userId, Integer from, Integer size) {

        List<Event> events = repository.findAllByInitiatorId(userId,
                PageRequest.of(from / size, size)).toList();

        events.forEach(this::addView);

        return mapper.toShortDtoList(events);
    }

    @Override
    public EventDto get(Long id, HttpServletRequest request) {

        Event event = repository.findByIdAndPublishedOnIsNotNull(id).orElseThrow(
                () -> {
                    log.error("Event with id=" + id + " does`t exist");
                    throw new EntityExistException("Event with id=" + id + " does`t exist");
                });

        if (!EventState.PUBLISHED.equals(event.getState())) {

            throw new EntityExistException("Event with id = " + id + "not found.");
        }

        addView(event);

        sendStats(event, request);

        return mapper.toDto(event);
    }

    @Override
    public EventDto getByUser(Long userId, Long eventId) {

        return mapper.toDto(repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> {
                    log.error("Event with id=" + eventId + " does`t exist");
                    throw new EntityExistException("Event with id=" + eventId + " does`t exist");
                }));
    }

    @Override
    public List<EventDto> getWithParametersByUser(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Boolean available,
                                                  SortState sort,
                                                  Integer from,
                                                  Integer size,
                                                  HttpServletRequest request) {

        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        LocalDateTime start;

        LocalDateTime end;

        try {
            start = rangeStart == null ?
                    null :
                    LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            end = rangeEnd == null ?
                    null :
                    LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (start != null && end != null && start.isAfter(end)) {
                throw new TimeIntervalException("Start date must be before end date.");
            }
        } catch (DateTimeParseException e) {

            throw new TimeIntervalException("Time format exception");
        }

        if (text != null) {
            criteria = builder.and(criteria, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (categories != null && categories.size() > 0) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (paid != null) {
            Predicate predicate;

            if (paid) predicate = builder.isTrue(root.get("paid"));

            else predicate = builder.isFalse(root.get("paid"));

            criteria = builder.and(criteria, predicate);
        }

        if (rangeEnd != null) {

            criteria = builder.and(criteria,
                    builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }

        if (rangeStart != null) {

            criteria = builder.and(criteria,
                    builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (available) {

            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {

            if (SortState.EVENT_DATE.equals(sort)) {

                events = events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            }
            else {

                events = events.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .collect(Collectors.toList());
            }
        }

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        sendStats(events, request);

        return mapper.toDtoList(events);

    }

    @Override
    public List<EventDto> getWithParametersByAdmin(List<Long> users,
                                                  EventState states,
                                                  List<Long> categories,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Integer from,
                                                  Integer size) {

        var builder = entityManager.getCriteriaBuilder();

        var query = builder.createQuery(Event.class);

        var root = query.from(Event.class);

        var criteria = builder.conjunction();

        LocalDateTime start;

        LocalDateTime end;

        try {

            start = rangeStart == null ?
                    null : LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            end = rangeEnd == null ?
                    null : LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {

            log.error(e.getMessage());

            throw new EventTimeException("Date string parsing error.");
        }


        if (rangeStart != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }

        if (rangeEnd != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }

        if (categories != null && categories.size() > 0) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (users != null && users.size() > 0) {
            criteria = builder.and(criteria, root.get("initiator").in(users));
        }

        if (states != null) {
            criteria = builder.and(criteria, root.get("state").in(states));
        }

        query.select(root).where(criteria);

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        return mapper.toDtoList(events);
    }

    @Override
    @Transactional
    public EventDto updateByUser(Long userId, Long eventId, EventUpdateDto dto) {

        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EventNotExistException("Event#" + eventId + " does not exist"));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserExistException("User with id = " + userId + " does`t exist."));

        if (!event.getInitiator().equals(user))
            throw new UserAccessException("This user does not have permissions to edit the event.");

        if (dto == null) {
            return mapper.toDto(event);
        }

        if (event.getPublishedOn() != null)
            throw new EventPublishException("Event has been published");

        if (event.getState().equals(EventState.PUBLISHED))
            throw new EventStateException("Only pending/canceled events can be edited.");

        if (dto.getStateAction() != null) {

            switch (dto.getStateAction()) {

                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;

                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);

                default:
                    throw new EventStateException("Unknown event state action.");
            }
        }

        if (dto.getEventDate() != null) {

            LocalDateTime eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now().plusHours(2)))
                throw new TimeIntervalException("Wrong time");

            event.setEventDate(dto.getEventDate());
        }

        checkEventDto(event, dto);

        return mapper.toDto(repository.save(event));
    }

    @Override
    @Transactional
    public EventDto updateByAdmin(Long eventId, AdminEventUpdateDto dto) {

        Event event = repository.findById(eventId).orElseThrow(
                () -> new EventNotExistException("Event#" + eventId + " does not exist"));

        if (dto == null)
            return mapper.toDto(event);

        LocalDateTime now = LocalDateTime.now();

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(now.plusHours(1))) {

            throw new EventTimeException("Event start time should be in future.");
        }

        if (dto.getAdminStateAction() != null) {

            if (!event.getState().equals(EventState.PENDING)) {

                throw new EventStateException("Only pending/canceled events can be edited.");
            }

            switch (dto.getAdminStateAction()) {

                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;

                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(now);
                    break;

                default:
                    throw new EventPublishException("Unknown event state action.");
            }
        }

        checkEventDto(event, dto);

        return mapper.toDto(repository.save(event));
    }

    private void checkEventDto(Event event, EventUpdateDto dto) {

        if (dto.getAnnotation() != null)
            event.setAnnotation(dto.getAnnotation());

        if (dto.getCategory() != null) {

            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> new CategoryExistsException("This category does not exist"));

            event.setCategory(category);
        }

        if (dto.getDescription() != null)
            event.setDescription(dto.getDescription());

        if (dto.getLocation() != null)
            event.setLocation(dto.getLocation());

        if (dto.getPaid() != null)
            event.setPaid(dto.getPaid());

        if (dto.getParticipantLimit() != null)
            event.setParticipantLimit(dto.getParticipantLimit().intValue());

        if (dto.getRequestModeration() != null)
            event.setRequestModeration(dto.getRequestModeration());

        if (dto.getTitle() != null)
            event.setTitle(dto.getTitle());
    }

    private void addView(Event event) {

        String start = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStatsDto> stats = client.getStats(start, end, uris, true);

        if (!stats.isEmpty()) {

            event.setViews(stats.size() == 2 ?
                    stats.get(1).getHits() :
                    stats.get(0).getHits());
        } else {

            event.setViews(0L);
        }
    }

    private void sendStats(Event event, HttpServletRequest request) {

        LocalDateTime now = LocalDateTime.now();

        HitDto hit = HitDto.builder()
                .ip(request.getRemoteAddr())
                .app("main")
                .uri("/events")
                .timestamp(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        client.addStats(hit);

        sendStatsForEvent(event.getId(), request.getRemoteAddr(), now);
    }

    private void sendStats(List<Event> events, HttpServletRequest request) {

        LocalDateTime now = LocalDateTime.now();

        HitDto requestDto = HitDto.builder()
                .ip(request.getRemoteAddr())
                .app("main")
                .uri("/events")
                .timestamp(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        client.addStats(requestDto);

        sendStatsForEvents(events, request.getRemoteAddr(), now);
    }

    private void sendStatsForEvent(Long eventId, String remoteAddress, LocalDateTime now) {

        HitDto hit = HitDto.builder()
                .ip(remoteAddress)
                .app("main")
                .uri("/events/" + eventId)
                .timestamp(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        client.addStats(hit);
    }

    private void sendStatsForEvents(List<Event> events, String remoteAddress, LocalDateTime now) {

        for (Event event : events) {

            HitDto requestDto = HitDto.builder()
                    .ip(remoteAddress)
                    .app("main")
                    .uri("/events/" + event.getId())
                    .timestamp(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            client.addStats(requestDto);
        }
    }
}
