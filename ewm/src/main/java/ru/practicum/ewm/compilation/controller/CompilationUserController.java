package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.user.CompilationUserService;

import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.COMPILATION_REQUEST;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationUserController {

    private final CompilationUserService service;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(required = false) Boolean pinned) {

        log.info(COMPILATION_REQUEST, "get with size restricted and pinned");

        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable Long compId) {

        log.info(COMPILATION_REQUEST, "get by id");

        return service.getCompilationById(compId);
    }
}
