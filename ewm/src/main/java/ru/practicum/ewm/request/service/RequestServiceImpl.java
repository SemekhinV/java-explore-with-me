package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static ru.practicum.ewm.event.enums.EventState.PUBLISHED;
import static ru.practicum.ewm.request.enums.RequestStatus.*;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getAllUsersRequest(Long userId) {

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id = " + userId + " не найден."));


        return requestMapper.toDtoList(requestRepository.findAllByRequester(userId));
    }

    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {

            throw new EntityConflictException("Данный пользователь уже оставил запрос на участие в указанном событии.");
        }

        Event event = checkUserAndEventExist(userId, eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {

            throw new EntityConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (PUBLISHED != event.getState()) {

            throw new EntityConflictException("Выбранное событие еще не опубликовано");
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);

        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {

            throw new EntityConflictException("В выбранном событии достигнут лимит запросов на участие.");
        }

        var result = new Request(null, event.getRequestModeration() && event.getParticipantLimit() > 0 ?
                PENDING
                : CONFIRMED, eventId, userId, now());

        return requestMapper.toDto(requestRepository.save(result));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatusOfUserEvent(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequest updateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow();

        List<Request> requests = requestRepository.findAllByEventWithInitiator(userId, eventId);

        List<Request> requestsToUpdate = requests.stream()
                .filter(s -> updateDto.getRequestIds().contains(s.getId()))
                .collect(Collectors.toList());

        if (requestsToUpdate.stream().anyMatch(request -> !PENDING.equals(request.getStatus()))) {

            throw new EntityConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit()) {

            requestsToUpdate.forEach(s -> s.setStatus(RequestStatus.REJECTED));
        }

        requestRepository.saveAll(requestsToUpdate);

        var result = new EventRequestStatusUpdateResult();

        switch (updateDto.getStatus()) {

            case CONFIRMED: {

                requestsToUpdate.forEach(s -> s.setStatus(CONFIRMED));
                result.setConfirmedRequests(requestMapper.toDtoList(requestsToUpdate));
                break;
            }

            case REJECTED: {

                requestsToUpdate.forEach(s -> s.setStatus(REJECTED));
                result.setConfirmedRequests(requestMapper.toDtoList(requestsToUpdate));
                break;
            }
        }

        return result;
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long requestId) {

        Request request = requestRepository.findByRequesterAndId(userId, requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос на участие с id = " + requestId + " не найден."));

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfCurrentUserByEventIdAndUserId(Long userId, Long eventId) {

        return requestMapper.toDtoList(requestRepository.findAllByEventWithInitiator(userId, eventId));
    }

    private Event checkUserAndEventExist(Long userId, Long eventId) {

        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        return event;
    }
}
