package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.EntityExistException;
import ru.practicum.ewm.error.exception.EventStateException;
import ru.practicum.ewm.error.exception.EventTimeException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.AdminStateAction;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventJpaRepository;
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

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    @Override
    public EventDto save(Long userId, EventRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new EntityExistException("Category does`t exist."));

        LocalDateTime date = dto.getEventDate();

        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventTimeException("Event date should be in future.");
        }

        Event event = mapper.toEvent(dto);

        event.setCategory(category);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityExistException("User with id=" + userId + " does`t exist."));

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
                () -> new EntityExistException("Event with id=" + id + " does`t exist"));

        addView(event);

        sendStats(List.of(event), request);

        return mapper.toDto(event);
    }

    @Override
    public EventDto getByUser(Long userId, Long eventId) {

        return mapper.toDto(repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityExistException("Event with id=" + eventId + " does`t exist")));
    }

    @Override
    public List<EventDto> getWithParametersByAdmin(GetWithParametersDto dto) {

        dto.setStartDate(
                dto.getRangeStart() == null ?
                        null :
                        LocalDateTime.parse(dto.getRangeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        dto.setEndDate(
                dto.getRangeEnd() == null ?
                        null :
                        LocalDateTime.parse(dto.getRangeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return mapper.toDtoList(repository.getByInitiator(dto));
    }

    @Override
    public List<EventDto> getWithParametersByUser(GetWithParametersDto dto, HttpServletRequest request) {

        dto.setStartDate(
                dto.getRangeStart() == null ?
                        null :
                        LocalDateTime.parse(dto.getRangeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        dto.setEndDate(
                dto.getRangeEnd() == null ?
                        null :
                        LocalDateTime.parse(dto.getRangeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<Event> response = repository.getByCategory(dto);

        sendStats(response, request);

        return mapper.toDtoList(response);
    }

    @Override
    public EventDto update(Long userId, Long eventId, EventUpdateDto dto) {

        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EntityExistException("Event with id=" + eventId + " does`t exist"));

        if (dto == null) {
            return  mapper.toDto(event);
        }

        if (event.getPublishedOn() != null) {
            throw new EventStateException("Event has been published.");
        }

        Category category = dto.getCategory() != null ?
                categoryRepository.findById(dto.getCategory()).orElseThrow(
                        () -> new EntityExistException("This category does`t exist."))
                : null;

        event.toBuilder()
                .annotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation())
                .category(category != null ? category : event.getCategory())
                .description(dto.getDescription() != null ? dto.getDescription() : event.getDescription())
                .location(dto.getLocation() != null ? dto.getLocation() : event.getLocation())
                .paid(dto.getPaid() != null ? dto.getPaid() : event.getPaid())
                .participantLimit(dto.getParticipantLimit() != null ?
                        dto.getParticipantLimit()
                        : event.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() != null ?
                        dto.getRequestModeration()
                        : event.getRequestModeration())
                .title(dto.getTitle() != null ? dto.getTitle() : event.getTitle())
                .build();

        if (dto.getStateAction() != null) {

            if (StateAction.SEND_TO_REVIEW.equals(dto.getStateAction())) {

                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }

        if (dto.getEventDate() != null) {

            LocalDateTime eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now()) || eventTime.isBefore(event.getPublishedOn().plusHours(1))) {

                throw new EventStateException("Wrong event publish time");
            }

            event.setEventDate(dto.getEventDate());
        }

        return mapper.toDto(repository.save(event));
    }

    @Override
    public EventDto update(Long eventId, AdminEventUpdateDto dto) {

        Event event = repository.findById(eventId).orElseThrow(
                () -> new EntityExistException("Event with id=" + eventId + " does not exist"));

        if (dto == null) {
            return mapper.toDto(event);
        }

        Category category = dto.getCategory() != null ?
                categoryRepository.findById(dto.getCategory()).orElseThrow(
                        () -> new EntityExistException("This category does`t exist."))
                : null;

        event.toBuilder()
                .annotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation())
                .category(category != null ? category : event.getCategory())
                .description(dto.getDescription() != null ? dto.getDescription() : event.getDescription())
                .location(dto.getLocation() != null ? dto.getLocation() : event.getLocation())
                .paid(dto.getPaid() != null ? dto.getPaid() : event.getPaid())
                .participantLimit(dto.getParticipantLimit() != null ?
                        dto.getParticipantLimit()
                        : event.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() != null ?
                        dto.getRequestModeration()
                        : event.getRequestModeration())
                .title(dto.getTitle() != null ? dto.getTitle() : event.getTitle())
                .build();

        if (dto.getStateAction() != null) {

            if (AdminStateAction.PUBLISH_EVENT.equals(dto.getStateAction())) {

                if (event.getPublishedOn() != null) {

                    throw new EventStateException("Event has been published");
                }

                if (EventState.CANCELED.equals(event.getState())) {

                    throw new EventStateException("Event has been canceled");
                }

                event.setState(EventState.PUBLISHED);

                event.setPublishedOn(LocalDateTime.now());
            } else if (AdminStateAction.REJECT_EVENT.equals(dto.getStateAction())) {

                if (event.getPublishedOn() != null) {
                    throw new EventStateException("Reject event has been published");
                }

                event.setState(EventState.CANCELED);
            }
        }

        if (dto.getEventDate() != null) {

            LocalDateTime eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now())
                    || eventTime.isBefore(event.getPublishedOn().plusHours(2))) {

                throw new EventStateException("Wrong event publish time");
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
