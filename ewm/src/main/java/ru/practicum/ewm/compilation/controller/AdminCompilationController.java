package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminCompilationController {

    private final CompilationService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/compilations")
    public CompilationDto save(@Valid @RequestBody CompilationRequestDto dto) {

        return service.save(dto);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto update(@Valid @RequestBody CompilationUpdateDto dto,
                                 @PathVariable Long compId) {

        return service.update(compId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/compilations/{compId}")
    public void delete(@PathVariable Long compId) {

        service.delete(compId);
    }
}
