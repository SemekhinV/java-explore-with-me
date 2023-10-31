package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.ShortEvenDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestUpdateDto;
import ru.practicum.ewm.request.dto.RequestUpdateResponseDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateEventController {

    private final RequestService requestService;
    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/events")
    public EventDto save(@Valid @RequestBody EventRequestDto dto,
                         @PathVariable Long userId) {

        return eventService.save(userId, dto);
    }

    @GetMapping("/events")
    public List<ShortEvenDto> getEventsByUser(@RequestParam(required = false, defaultValue = "10") Integer size,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @PathVariable Long userId) {

        return eventService.get(userId, from, size);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getRequestsByOwnerOfEvent(@PathVariable Long eventId,
                                                      @PathVariable Long userId) {

        return requestService.getByOwnerOfEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public RequestUpdateResponseDto updateRequestsByAdmin(@RequestBody RequestUpdateDto requestUpdateDto,
                                                          @PathVariable Long eventId,
                                                          @PathVariable Long userId) {

        return requestService.updateRequest(userId, eventId, requestUpdateDto);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEventByUser(@Valid @RequestBody EventUpdateDto updateEventUserDto,
                                          @PathVariable Long eventId,
                                          @PathVariable Long userId) {

        return eventService.update(userId, eventId, updateEventUserDto);
    }

    @GetMapping("/events/{eventId}")
    public EventDto getEventByUser(@PathVariable Long eventId,
                                   @PathVariable Long userId) {

        return eventService.getByUser(userId, eventId);
    }
}
