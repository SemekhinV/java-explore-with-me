package ru.practicum.ewm.event.service.local;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.BadInputParametersException;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.request.enums.RequestStatus.PENDING;
import static ru.practicum.ewm.request.enums.RequestStatus.REJECTED;
import static ru.practicum.ewm.util.EwmPatterns.URI;
import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMATTER;

@Service
@Transactional
@AllArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository repository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    private final LocationRepository locationRepository;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final RequestService requestService;

    @Override
    public EventFullDto save(Long userId, NewEventDto dto) {

        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        var category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new EntityNotFoundException("Категория с id " + dto.getCategory()  + " не найдена."));

        var eventDate = dto.getEventDate();

        if (eventDate == null || eventDate.isBefore(LocalDateTime.now().plusHours(2))) {

            throw new EntityConflictException("Дата события должна быть в будущем.");
        }

        var location = locationRepository.findByLatAndLon(dto.getLocation().getLat(), dto.getLocation().getLat())
                .orElseGet(() -> locationRepository.save(
                        new Location(null, dto.getLocation().getLat(), dto.getLocation().getLon())));

        var event = mapper.toEvent(dto);

        event.setLocation(location);
        event.setCategory(category);
        event.setInitiator(user);

        var result = mapper.toDto(repository.save(event));

        result.setViews(0L);
        result.setConfirmedRequests(0L);

        return result;
    }

    @Override
    public List<EventShortDto> getAllUserEvents(Long userId, int from, int size) {

        var events = repository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));

        return mapToShortDtoAndAddView(events.toList());
    }

    @Override
    public EventFullDto getCurrentUserEventByEventId(Long userId, Long eventId) {

        var event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EntityNotFoundException("Событие с переданными параметрами не найдено."));

        var result = mapper.toDto(event);

        result.setViews(getViews(event));

        requestService.setConfirmedRequestCountFull(List.of(result));

        return result;
    }

    @Override
    public EventFullDto updateCurrentUserEventByEventId(UpdateEventUserRequest dto) {

        var event = checkEventAndUserExist(dto.getUserId(), dto.getEventId());

        if (event.getState().equals(EventState.PUBLISHED)) {

            throw new EntityConflictException("Опубликованное событие невозможно редактировать.");
        }

        if (dto.getStateAction() != null) {

            switch (dto.getStateAction()) {

                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;

                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;

                default:
                    throw new BadInputParametersException("Переданное состояние события не обрабатывается.");
            }
        }

        if (dto.getCategory() != null) {

            var category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> new EntityExistsException("Указанная категория не найдена."));

            event.setCategory(category);
        }

        if (dto.getEventDate() != null) {

            LocalDateTime eventTime = dto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {

                throw new EntityConflictException("Время, на которые намечено событие, не может быть раньше, чем через"
                        + " два часа от текущего момента");
            }

            event.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {

            event.setLocation(getLocation(dto));
        }

        updateEventValues(event, dto);

        var result = mapper.toDto(repository.save(event));

        result.setViews(getViews(event));

        requestService.setConfirmedRequestCountFull(List.of(result));

        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfCurrentUserByEventIdAndUserId(Long userId, Long eventId) {

        checkEventAndUserExist(userId, eventId);

        var result = requestRepository.findAllByEventWithInitiator(userId, eventId);

        return requestMapper.toDtoList(result);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest dto) {

        var event = checkEventAndUserExist(dto.getUserId(), dto.getEventId());

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {

            throw new EntityConflictException("Подтверждение заявок не требуется.");
        }

        var confirmedRequest = requestRepository.getEventConfirmedRequestCount(event.getId());

        if (event.getParticipantLimit().equals(confirmedRequest)) {

            throw new EntityConflictException("Достигнут лимит по заявкам.");
        }

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(dto.getRequestIds());

        if (requestsToUpdate.stream().anyMatch(request -> !PENDING.equals(request.getStatus()))) {

            throw new EntityConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (event.getParticipantLimit() - confirmedRequest <= 0) {

            requestsToUpdate.forEach(s -> s.setStatus(RequestStatus.REJECTED));
        }

        var result = new EventRequestStatusUpdateResult();

        switch (dto.getStatus()) {

            case CONFIRMED: {

                requestsToUpdate.forEach(s -> s.setStatus(RequestStatus.CONFIRMED));

                result.setConfirmedRequests(requestMapper.toDtoList(requestsToUpdate));

                break;
            }

            case REJECTED: {

                requestsToUpdate.forEach(s -> s.setStatus(REJECTED));

                result.setRejectedRequests(requestMapper.toDtoList(requestsToUpdate));

                break;
            }
        }

        repository.save(event);

        requestRepository.saveAll(requestsToUpdate);

        return result;
    }

    private Event checkEventAndUserExist(Long userId, Long eventId) {

        var event = repository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        if (!event.getInitiator().equals(user)) {

            throw new EntityNotFoundException("Выбранное событие не принадлежит указанному пользователю.");
        }

        return event;
    }

    private Location getLocation(UpdateEventUserRequest dto) {

        var lat = dto.getLocation().getLat();

        var lon = dto.getLocation().getLon();

        return locationRepository.findByLatAndLon(lat, lon).orElseGet(
                () -> locationRepository.save(new Location(null, lat, lon)));
    }

    private void updateEventValues(Event event, UpdateEventUserRequest dto) {

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {

            event.setTitle(dto.getTitle());
        }

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {

            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {

            event.setDescription(dto.getDescription());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getRequestModeration() != null) {

            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getParticipantLimit() != null) {

            event.setParticipantLimit(dto.getParticipantLimit());
        }
    }

    private Long getViews(Event event) {

        var start = event.getCreatedOn() == null ?
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
                : event.getCreatedOn().format(DATE_TIME_FORMATTER);

        var end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var stats = client.getStats(start, end, List.of("/events/" + event.getId()), true);

        if (stats != null && !stats.isEmpty()) {

            return stats.get(0).getHits();
        } else {

            return 0L;
        }
    }

    private List<EventShortDto> mapToShortDtoAndAddView(List<Event> events) {

        var result = setViews(events);

        requestService.setConfirmedRequestCountShort(result);

        return result;
    }

    private List<EventShortDto> setViews(List<Event> events) {

        var start = LocalDateTime.now();

        var end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var uris = new ArrayList<String>();

        for (var event : events) {

            if (event.getCreatedOn() != null && event.getCreatedOn().isBefore(start)) {

                start = event.getCreatedOn();
            }

            uris.add(URI + event.getId());
        }

        var result = mapper.toShortDtoList(events);

        var stats = client.getStats(start.format(DATE_TIME_FORMATTER), end, uris, true);

        if (stats.isEmpty()) {

            result.forEach(event -> event.setViews(0L));
        }

        var viewMap = getViewsMap(stats);

        result.forEach(event -> {

            if (viewMap.getOrDefault(event.getId(), 0L) != 0) {

                event.setViews(viewMap.get(event.getId()));
            }
        });

        return result;
    }

    private Map<Long, Long> getViewsMap(List<ViewStatsDto> stats) {

        var map = new HashMap<Long, Long>();

        for (var stat : stats) {

            map.put(getEventIdFromStats(stat.getUri()), stat.getHits());
        }

        return map;
    }

    private Long getEventIdFromStats(String uri) {

        return NumberUtils.toLong(StringUtils.replace(uri, URI, ""));
    }
}
