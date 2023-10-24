package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationDto save(CompilationRequestDto savedCompilationDto);

    CompilationDto get(Long compId);

    List<CompilationDto> get(Boolean pinned, Integer from, Integer size);

    CompilationDto update(Long compId, CompilationUpdateDto compilationUpdateRequest);

    void delete(Long compId);
}
