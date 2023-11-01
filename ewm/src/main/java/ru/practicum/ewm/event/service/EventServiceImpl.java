package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.EntityExistException;
import ru.practicum.ewm.error.exception.EventPublishException;
import ru.practicum.ewm.error.exception.EventStateException;
import ru.practicum.ewm.error.exception.EventTimeException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.AdminStateAction;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventJpaRepository repository;

    private final EventRepository getEventWithParameters;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    @Override
    @Transactional
    public EventDto save(Long userId, EventRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> {
                    log.error("Category does`t exist.");
                    throw new EntityExistException("Category does`t exist.");
                });

        LocalDateTime date = dto.getEventDate();

        if (date.isBefore(LocalDateTime.now().plusHours(2))) {

            log.error("Event date should be in future.");

            throw new EventTimeException("Event date should be in future.");
        }

        Event event = mapper.toEvent(dto);

        event.setCategory(category);

        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User with id=" + userId + " does`t exist.");
                    throw new EntityExistException("User with id=" + userId + " does`t exist.");
                });

        event.setInitiator(user);

        return mapper.toDto(repository.save(event));
    }

    @Override
    public List<ShortEvenDto> get(Long userId, Integer from, Integer size) {

        List<Event> events = repository.findAllByInitiatorId(userId,
                PageRequest.of(from / size, size)).toList();

        return mapper.toShortDtoList(events);
    }

    @Override
    public EventDto get(Long id, HttpServletRequest request) {

        Event event = repository.findByIdAndPublishedOnIsNotNull(id).orElseThrow(
                () -> {
                    log.error("Event with id=" + id + " does`t exist");
                    throw new EntityExistException("Event with id=" + id + " does`t exist");
                });

        addView(event);

        sendStats(List.of(event), request);

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
    public List<EventDto> getWithParametersByAdmin(AdminDtoWithParameters dto) {

        LocalDateTime start = dto.getRangeStart() == null ?
                null :
                LocalDateTime.parse(dto.getRangeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime end = dto.getRangeEnd() == null ?
                null :
                LocalDateTime.parse(dto.getRangeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        var d = getEventWithParameters.admin(dto, start, end);

        return mapper.toDtoList(d);
    }

    @Override
    public List<EventDto> getWithParametersByUser(UserDtoWithParameters dto, HttpServletRequest request) {

        LocalDateTime start = dto.getDateStart() == null ?
                null :
                LocalDateTime.parse(dto.getDateStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime end = dto.getDateStart() == null ?
                null :
                LocalDateTime.parse(dto.getDateEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<Event> response = getEventWithParameters.user(dto, start, end);

        sendStats(response, request);

        return mapper.toDtoList(response);
    }

    @Override
    @Transactional
    public EventDto update(Long userId, Long eventId, EventUpdateDto dto) {

        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> {
                    log.error("Event with id=" + eventId + " does`t exist");
                    throw new EntityExistException("Event with id=" + eventId + " does`t exist");
                });

        if (dto == null) {

            return mapper.toDto(event);
        }

        if (event.getPublishedOn() != null) {

            throw new EventPublishException("Event has been published");
        }

        if (dto.getAnnotation() != null) {

            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {

            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> {
                        log.error("This category does not exist");
                        throw new EntityExistException("This category does not exist");
                    });

            event.setCategory(category);
        }

        if (dto.getDescription() != null) {

            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {

            LocalDateTime eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {

                log.error("Wrong time");

                throw new EventTimeException("Wrong time");
            }

            event.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {

            event.setLocation(dto.getLocation());
        }

        if (dto.getPaid() != null) {

            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {

            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {

            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {

            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() != null) {

            if (StateAction.SEND_TO_REVIEW.equals(dto.getStateAction())) {

                event.setState(EventState.PENDING);
            } else {

                event.setState(EventState.CANCELED);
            }
        }

        return mapper.toDto(repository.save(event));
    }

    @Override
    @Transactional
    public EventDto update(Long eventId, AdminEventUpdateDto dto) {

        Event event = repository.findById(eventId).orElseThrow(
                () -> {
                    log.error("Event with id=" + eventId + " does not exist");
                    throw new EntityExistException("Event with id=" + eventId + " does not exist");
                });

        if (dto == null) {

            return mapper.toDto(event);
        }

        if (dto.getAnnotation() != null) {

            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {

            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> {
                        log.error("This category does not exist");
                        throw new EntityExistException("This category does not exist");
                    });

            event.setCategory(category);
        }

        if (dto.getDescription() != null) {

            event.setDescription(dto.getDescription());
        }

        if (dto.getLocation() != null) {

            event.setLocation(dto.getLocation());
        }

        if (dto.getPaid() != null) {

            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {

            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {

            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {

            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() != null) {

            if (AdminStateAction.PUBLISH_EVENT.equals(dto.getStateAction())) {

                if (event.getPublishedOn() != null) {

                    log.error("Event has been published");

                    throw new EventPublishException("Event has been published");
                }

                if (EventState.CANCELED.equals(event.getState())) {

                    log.error("Event has been canceled");

                    throw new EventStateException("Event has been canceled");
                }

                event.setState(EventState.PUBLISHED);

                event.setPublishedOn(LocalDateTime.now());
            } else if (AdminStateAction.REJECT_EVENT.equals(dto.getStateAction())) {

                if (event.getPublishedOn() != null) {

                    log.error("Event has been published");

                    throw new EventPublishException("Event has been published");
                }

                event.setState(EventState.CANCELED);
            }
        }

        if (dto.getEventDate() != null) {

            var eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now()) || eventTime.isBefore(event.getPublishedOn().plusHours(1))) {

                log.error("Wrong time");

                throw new EventTimeException("Wrong time");
            }

            event.setEventDate(dto.getEventDate());
        }

        return mapper.toDto(repository.save(event));
    }

    private void addView(Event event) {

        String start = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStatsDto> stats = client.getStats(start, end, uris, null);

        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }
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
