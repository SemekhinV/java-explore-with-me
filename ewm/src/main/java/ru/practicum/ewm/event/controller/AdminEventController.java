package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminDtoWithParameters;
import ru.practicum.ewm.event.dto.AdminEventUpdateDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) EventState states,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) String rangeEnd) {

        return eventService.getWithParametersByAdmin(new AdminDtoWithParameters(
                from, size, categories, states, rangeStart, users, rangeEnd));
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEvent(@Valid @RequestBody AdminEventUpdateDto dto,
                                    @PathVariable Long eventId) {

        return eventService.updateByAdmin(eventId, dto);
    }
}
