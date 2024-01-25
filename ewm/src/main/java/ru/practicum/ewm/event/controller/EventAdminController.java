package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.service.admin.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMAT;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventAdminService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                            LocalDateTime rangeStart,
                                        @RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                            LocalDateTime rangeEnd) {

        return service.getWithParametersByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest dto,
                                    @PathVariable Long eventId) {

        return service.editEventByAdmin(eventId, dto);
    }
}
