package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static ru.practicum.ewm.event.enums.EventState.PUBLISHED;
import static ru.practicum.ewm.request.enums.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.request.enums.RequestStatus.PENDING;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getAllUsersRequest(Long userId) {

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id = " + userId + " не найден."));


        return requestMapper.toDtoList(repository.findAllByRequester(userId));
    }

    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {

        if (repository.existsByRequesterAndEvent(userId, eventId)) {

            throw new EntityConflictException("Данный пользователь уже оставил запрос на участие в указанном событии.");
        }

        Event event = checkUserAndEventExist(userId, eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {

            throw new EntityConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (PUBLISHED != event.getState()) {

            throw new EntityConflictException("Выбранное событие еще не опубликовано");
        }

        List<Request> requests = repository.findAllByEvent(eventId);

        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {

            throw new EntityConflictException("В выбранном событии достигнут лимит запросов на участие.");
        }

        var result = new Request(null, event.getRequestModeration() && event.getParticipantLimit() > 0 ?
                PENDING
                : CONFIRMED, eventId, userId, now());

        return requestMapper.toDto(repository.save(result));
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long requestId) {

        Request request = repository.findByRequesterAndId(userId, requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос на участие с id = " + requestId + " не найден."));

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(repository.save(request));
    }

    private Event checkUserAndEventExist(Long userId, Long eventId) {

        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        return event;
    }

    @Override
    public void setConfirmedRequestCountFull(List<EventFullDto> events) {

        var confirmedRequestsMap = getConfrimedRequestMap(getIdsFull(events));

        events.forEach(event -> event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L)));
    }

    private Map<Long, Long> getConfrimedRequestMap(List<Long> events) {

        var confirmedRequests = repository.getConfirmedRequestsCount(events);

        var confirmedRequestsMap = new HashMap<Long, Long>();

        for (var confirmedRequest : confirmedRequests) {

            if (confirmedRequestsMap.containsKey(confirmedRequest.getEventId()) ) {

                var count = confirmedRequestsMap.get(confirmedRequest.getEventId());

                confirmedRequestsMap.put(confirmedRequest.getEventId(), count + confirmedRequest.getRequestsCount());
            } else {

                confirmedRequestsMap.put(confirmedRequest.getEventId(), confirmedRequest.getRequestsCount());
            }
        }

        return confirmedRequestsMap;
    }

    @Override
    public void setConfirmedRequestCountShort(List<EventShortDto> events) {

        var confirmedRequestsMap = getConfrimedRequestMap(getIdsShort(events));

        events.forEach(event -> event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L)));
    }

    private List<Long> getIdsFull(List<EventFullDto> events) {

        return events.stream().map(EventFullDto::getId).collect(Collectors.toList());
    }

    private List<Long> getIdsShort(List<EventShortDto> events) {

        return events.stream().map(EventShortDto::getId).collect(Collectors.toList());
    }
}
