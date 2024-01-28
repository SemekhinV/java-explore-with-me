package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static ru.practicum.ewm.util.EwmPatterns.EVENT_REQUEST;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto save(@Valid @RequestBody NewEventDto dto, @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "create");

        return service.save(userId, dto);
    }

    @GetMapping
    public List<EventShortDto> getAllUserEvents(@RequestParam(required = false, defaultValue = "10") Integer size,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "private get with size restricted");

        return service.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getCurrentUserEventByEventId(@PathVariable Long eventId,
                                                     @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "private get by event id and user id");

        return service.getCurrentUserEventByEventId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerOfEvent(@PathVariable Long eventId,
                                                                   @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "private get request to event by owner");

        return service.getRequestsOfCurrentUserByEventIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsByAdmin(
            @RequestBody EventRequestStatusUpdateRequest requestUpdateDto,
            @PathVariable Long eventId,
            @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "update event requests status");

        requestUpdateDto.setEventId(eventId);

        requestUpdateDto.setUserId(userId);

        return service.updateRequestStatus(requestUpdateDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@Valid @RequestBody UpdateEventUserRequest updateEventUserDto,
                                          @PathVariable Long eventId,
                                          @PathVariable Long userId) {

        log.info(EVENT_REQUEST, "update by user");

        updateEventUserDto.setEventId(eventId);

        updateEventUserDto.setUserId(userId);

        return service.updateCurrentUserEventByEventId(updateEventUserDto);
    }
}
