package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.error.exception.BadInputParametersException;
import ru.practicum.ewm.event.dto.EventAdminFilters;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.service.admin.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.EVENT_REQUEST;
import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMAT;

@Slf4j
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

        log.info(EVENT_REQUEST, "get with parameters by admin");

        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {

            throw new BadInputParametersException("Время окончания не может быть раньше времени начала.");
        }

        var filters = new EventAdminFilters(size, from, categories, states, rangeStart, users, rangeEnd);

        return service.getWithParametersByAdmin(filters);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest dto,
                                    @PathVariable Long eventId) {

        log.info(EVENT_REQUEST, "get update by admin");

        dto.setId(eventId);

        return service.editEventByAdmin(dto);
    }
}
