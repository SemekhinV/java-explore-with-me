package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.UserDtoWithParameters;
import ru.practicum.ewm.event.enums.SortState;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService service;

    @GetMapping("/{id}")
    public EventDto get(@PathVariable Long id, HttpServletRequest request) {

        return service.get(id, request);
    }

    @GetMapping
    public List<EventDto> getByUser(@RequestParam(required = false, defaultValue = "10") Integer size,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(required = false) SortState sort,
                                    @RequestParam(required = false) Boolean paid,
                                    @RequestParam(required = false) String text,
                                    HttpServletRequest request) {

        return service.getWithParametersByUser(new UserDtoWithParameters(
                text, size, from, onlyAvailable, categories, rangeStart, rangeEnd, sort, paid),
                request);
    }
}
