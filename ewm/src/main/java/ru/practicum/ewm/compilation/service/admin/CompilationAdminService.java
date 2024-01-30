package ru.practicum.ewm.compilation.service.admin;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

public interface CompilationAdminService {

    CompilationDto save(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(UpdateCompilationRequest dto);
}
