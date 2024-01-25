package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.user.CompilationUserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationUserController {

    private final CompilationUserService service;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(required = false) Boolean pinned) {

        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable Long compId) {

        return service.getCompilationById(compId);
    }
}
