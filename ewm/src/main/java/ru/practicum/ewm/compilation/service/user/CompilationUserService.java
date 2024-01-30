package ru.practicum.ewm.compilation.service.user;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationUserService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);
}
