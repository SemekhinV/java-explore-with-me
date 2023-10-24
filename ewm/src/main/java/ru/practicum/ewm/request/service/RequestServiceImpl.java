package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;
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

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventJpaRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto save(Long userId, Long eventId) {

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {

            throw new EntityExistException("Request is already exist");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityExistException("Event with id=" + eventId + " does not exist"));

        if (event.getInitiator().getId().equals(userId)) {

            throw new UserAccessException("Request cannot be created by the initiator (id=" + userId + ")");
        }

        if (event.getPublishedOn() == null) {

            throw new EventPublishException("Event has not been published yet");
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);

        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {

            throw new ParticipantLimitException("Participant limit was exceeded");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(RequestStatus.PENDING)
                .build();

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> get(Long userId) {

        userRepository.findById(userId).orElseThrow(
                () -> new EntityExistException("User with id=" + userId + " does not exist"));

        return requestMapper.toDtoList(requestRepository.findAllByRequester(userId));
    }

    @Override
    public List<RequestDto> getByOwnerOfEvent(Long userId, Long eventId) {

        return requestMapper.toDtoList(requestRepository.findAllByEventIdAndOwnerId(userId, eventId));
    }

    @Override
    @Transactional
    public RequestUpdateResponseDto updateRequest(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityExistException("Event with id=" + eventId + " does not exist"));

        RequestUpdateResponseDto result = new RequestUpdateResponseDto();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {

            return result;
        }

        List<Request> requests = requestRepository.findAllByEventIdAndOwnerId(userId, eventId);

        List<Request> requestsToUpdate = requests.stream()
                .filter(val -> requestUpdateDto.getRequestIds().contains(val.getId()))
                .collect(Collectors.toList());

        if (requestsToUpdate.stream()
                .anyMatch(request -> RequestStatus.CONFIRMED.equals(request.getStatus())
                        && RequestOperationStatus.REJECTED.equals(requestUpdateDto.getStatus()))) {

            throw new RequestStateException("Request has been already confirmed");
        }

        if ((event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit())
                && RequestOperationStatus.CONFIRMED.equals(requestUpdateDto.getStatus())) {

            throw new ParticipantLimitException("Participants limit was exceeded");
        }

        for (Request request : requestsToUpdate) {

            request.setStatus(RequestStatus.valueOf(requestUpdateDto.getStatus().toString()));
        }

        requestRepository.saveAll(requestsToUpdate);

        if (RequestOperationStatus.CONFIRMED.equals(requestUpdateDto.getStatus()))
            event.setConfirmedRequests(requestsToUpdate.size() + event.getConfirmedRequests());

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
                () -> new EntityExistException("Request with id=" + requestId + " does not exist"));

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(requestRepository.save(request));
    }
}
