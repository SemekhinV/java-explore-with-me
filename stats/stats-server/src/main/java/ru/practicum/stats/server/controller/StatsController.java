package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.server.util.TimeFormatter.DATE_TIME_FORMAT;

@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid HitDto hitDto) {

        service.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                       @RequestParam(defaultValue = "false") boolean unique,
                                       @RequestParam(required = false) List<String> uris) {

        return service.getHits(start, end, uris, unique);
    }
}
