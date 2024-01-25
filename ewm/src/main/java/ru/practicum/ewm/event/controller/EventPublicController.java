package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.enums.EventSortState;
import ru.practicum.ewm.event.service.user.EventUserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.TimeFormatter.DATE_TIME_FORMAT;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    private final EventUserService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        return service.getEventById(id, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getByUser(@RequestParam(required = false, defaultValue = "10") @Min(1) Integer size,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                        @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                            LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                            LocalDateTime rangeEnd,
                                        @RequestParam(required = false) EventSortState sort,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false, defaultValue = "") String text,
                                        HttpServletRequest request) {

        return service.getWithParametersByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size,request);
    }
}
