package ru.practicum.ewm.event.service.local;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.enums.RequestOperationStatus.CONFIRMED;
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
        event.setConfirmedRequests(0L);

        var result = mapper.toDto(repository.save(event));

        result.setViews(0L);

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

        return result;
    }

    @Override
    public EventFullDto updateCurrentUserEventByEventId(Long userId, Long eventId, UpdateEventUserRequest updateDto) {

        var event = checkEventAndUserExist(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {

            throw new EntityConflictException("Опубликованное событие невозможно редактировать.");
        }

        if (updateDto.getStateAction() != null) {

            switch (updateDto.getStateAction()) {

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

        if (updateDto.getCategory() != null) {

            var category = categoryRepository.findById(updateDto.getCategory()).orElseThrow(
                    () -> new EntityExistsException("Указанная категория не найдена."));

            event.setCategory(category);
        }

        if (updateDto.getEventDate() != null) {

            LocalDateTime eventTime = updateDto.getEventDate();

            if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {

                throw new EntityConflictException("Время, на которые намечено событие, не может быть раньше, чем через"
                        + " два часа от текущего момента");
            }

            event.setEventDate(updateDto.getEventDate());
        }

        if (updateDto.getLocation() != null) {

            event.setLocation(getLocation(updateDto));
        }

        updateEventValues(event, updateDto);

        var result = mapper.toDto(repository.save(event));

        result.setViews(getViews(event));

        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfCurrentUserByEventIdAndUserId(Long userId, Long eventId) {

        checkEventAndUserExist(userId, eventId);

        return requestService.getRequestsOfCurrentUserByEventIdAndUserId(userId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateDto) {


        var event = checkEventAndUserExist(userId, eventId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {

            throw new EntityConflictException("Подтверждение заявок не требуется.");
        }

        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {

            throw new EntityConflictException("Достигнут лимит по заявкам.");
        }

        var requests = requestService.updateRequestsStatusOfUserEvent(userId,eventId, updateDto);

        if (CONFIRMED.equals(updateDto.getStatus())) {

            event.setConfirmedRequests(event.getConfirmedRequests() + requests.getConfirmedRequests().size());
        }

        repository.save(event);

        return requests;
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

        return events.stream().map(this::mapToShortWithViews).collect(Collectors.toList());
    }

    private EventShortDto mapToShortWithViews(Event event) {

        var start = event.getCreatedOn() == null ?
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
                : event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        var end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        var uris = List.of("/events/" + event.getId());

        var stats = client.getStats(start, end, uris, true);

        var shotDto = mapper.toShortDto(event);

        shotDto.setViews(!stats.isEmpty() ? stats.get(0).getHits() : 0L);

        return shotDto;
    }
}
