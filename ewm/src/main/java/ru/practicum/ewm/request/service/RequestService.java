package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto save(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllUsersRequest(Long userId);

    List<ParticipationRequestDto> getRequestsOfCurrentUserByEventIdAndUserId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatusOfUserEvent(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest requestUpdateDto);

    ParticipationRequestDto rejectRequest(Long userId, Long requestId);
}
