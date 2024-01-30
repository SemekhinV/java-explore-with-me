package ru.practicum.ewm.event.service.local;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventFullDto save(Long userId, NewEventDto dto);

    List<EventShortDto> getAllUserEvents(Long userId, int from, int size);

    EventFullDto getCurrentUserEventByEventId(Long userId, Long eventId);

    EventFullDto updateCurrentUserEventByEventId(UpdateEventUserRequest updateDto);

    List<ParticipationRequestDto> getRequestsOfCurrentUserByEventIdAndUserId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest updateDto);
}