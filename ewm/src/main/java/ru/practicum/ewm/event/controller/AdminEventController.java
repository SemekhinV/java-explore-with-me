package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminEventUpdateDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.GetWithParametersDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestParam(required = false, defaultValue = "10") Integer size,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) EventState states,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) String rangeEnd) {

        return eventService.getWithParametersByAdmin(new GetWithParametersDto(
                null,
                users,
                states,
                categories,
                null,
                rangeStart,
                null,
                rangeEnd,
                null,
                null,
                null,
                from,
                size));
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEvent(@Valid @RequestBody AdminEventUpdateDto dto,
                                    @PathVariable Long eventId) {

        return eventService.update(eventId, dto);
    }
}
