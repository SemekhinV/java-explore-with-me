package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.admin.CompilationAdminService;

import javax.validation.Valid;

import static ru.practicum.ewm.util.EwmPatterns.COMPILATION_REQUEST;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {

        log.info(COMPILATION_REQUEST, "create");

        return service.save(dto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationRequest dto, @PathVariable Long compId) {

        log.info(COMPILATION_REQUEST, "update");

        dto.setId(compId);

        return service.update(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {

        log.info(COMPILATION_REQUEST, "delete");

        service.delete(compId);
    }
}
