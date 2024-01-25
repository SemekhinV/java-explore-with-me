package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.local.EventPrivateService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto save(@Valid @RequestBody NewEventDto dto, @PathVariable Long userId) {

        return service.save(userId, dto);
    }

    @GetMapping
    public List<EventShortDto> getAllUserEvents(@RequestParam(required = false, defaultValue = "10") Integer size,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @PathVariable Long userId) {

        return service.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getCurrentUserEventByEventId(@PathVariable Long eventId,
                                                     @PathVariable Long userId) {

        return service.getCurrentUserEventByEventId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerOfEvent(@PathVariable Long eventId,
                                                                   @PathVariable Long userId) {

        return service.getRequestsOfCurrentUserByEventIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsByAdmin(
            @RequestBody EventRequestStatusUpdateRequest requestUpdateDto,
            @PathVariable Long eventId,
            @PathVariable Long userId) {

        return service.updateRequestStatus(userId, eventId, requestUpdateDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@Valid @RequestBody UpdateEventUserRequest updateEventUserDto,
                                          @PathVariable Long eventId,
                                          @PathVariable Long userId) {

        return service.updateCurrentUserEventByEventId(userId, eventId, updateEventUserDto);
    }
}
