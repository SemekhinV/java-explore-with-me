package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.event.EventParticipantLimitException;
import ru.practicum.ewm.error.exception.event.EventPublishException;
import ru.practicum.ewm.error.exception.request.RequestExistException;
import ru.practicum.ewm.error.exception.request.RequestStateException;
import ru.practicum.ewm.error.exception.user.UserAccessException;
import ru.practicum.ewm.error.exception.util.EntityExistException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestUpdateDto;
import ru.practicum.ewm.request.dto.RequestUpdateResponseDto;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.enums.RequestOperationStatus;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto save(Long userId, Long eventId) {

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {

            log.error("Request with userId=" + userId + " and eventId=" + eventId + " already exist.");

            throw new RequestExistException("Request is already exist");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.error("Event with id=" + eventId + " does not exist");
                    throw new EntityExistException("Event with id=" + eventId + " does not exist");
                });

        if (event.getInitiator().getId().equals(userId)) {

            log.error("Request cannot be created by the initiator (id=" + userId + ")");

            throw new UserAccessException("Request cannot be created by the initiator (id=" + userId + ")");
        }

        if (event.getPublishedOn() == null) {

            log.error("Event has not been published yet");

            throw new EventPublishException("Event has not been published yet");
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);

        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {

            log.error("Participant limit was exceeded");

            throw new EventParticipantLimitException("Participant limit was exceeded");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(event.getRequestModeration() && event.getParticipantLimit() > 0 ?
                                RequestStatus.PENDING :
                                RequestStatus.CONFIRMED)
                .build();

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> get(Long userId) {

        userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User with id=" + userId + " does`t exist");
                    throw new EntityExistException("User with id=" + userId + " does`t exist");
                });

        return requestMapper.toDtoList(requestRepository.findAllByRequester(userId));
    }

    @Override
    public List<RequestDto> getByOwnerOfEvent(Long userId, Long eventId) {

        return requestMapper.toDtoList(requestRepository.findAllByEventWithInitiator(userId, eventId));
    }

    @Override
    @Transactional
    public RequestUpdateResponseDto updateRequest(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.error("Event with id=" + eventId + " does not exist");
                    throw new EntityExistException("Event with id=" + eventId + " does not exist");
                });

        RequestUpdateResponseDto result = new RequestUpdateResponseDto();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {

            return result;
        }

        List<Request> requests = requestRepository.findAllByEventWithInitiator(userId, eventId);

        List<Request> requestsToUpdate = requests.stream()
                .filter(val -> requestUpdateDto.getRequestIds().contains(val.getId()))
                .collect(Collectors.toList());

        if (requestsToUpdate.stream()
                .anyMatch(request -> RequestStatus.CONFIRMED.equals(request.getStatus())
                        && RequestOperationStatus.REJECTED.equals(requestUpdateDto.getStatus()))) {

            log.error("Request has been already confirmed");

            throw new RequestStateException("Request has been already confirmed");
        }

        if ((event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit())
                && RequestOperationStatus.CONFIRMED.equals(requestUpdateDto.getStatus())) {

            log.error("Participants limit was exceeded");

            throw new EventParticipantLimitException("Participants limit was exceeded");
        }

        for (Request request : requestsToUpdate) {

            request.setStatus(RequestStatus.valueOf(requestUpdateDto.getStatus().toString()));
        }

        requestRepository.saveAll(requestsToUpdate);

        if (RequestOperationStatus.CONFIRMED.equals(requestUpdateDto.getStatus())) {

            event.setConfirmedRequests(requestsToUpdate.size() + event.getConfirmedRequests());
        }

        eventRepository.save(event);

        if (RequestOperationStatus.REJECTED.equals(requestUpdateDto.getStatus())) {

            result.setRejectedRequests(requestMapper.toDtoList(requestsToUpdate));
        }

        if (RequestOperationStatus.CONFIRMED.equals(requestUpdateDto.getStatus())) {

            result.setConfirmedRequests(requestMapper.toDtoList(requestsToUpdate));
        }

        return result;
    }

    @Override
    public RequestDto rejectRequest(Long userId, Long requestId) {

        Request request = requestRepository.findByRequesterAndId(userId, requestId).orElseThrow(
                () -> {
                    log.error("Request with id=" + requestId + " does not exist");
                    throw new EntityExistException("Request with id=" + requestId + " does not exist");
                });

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(requestRepository.save(request));
    }
}
