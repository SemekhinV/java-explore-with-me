package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.entity.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDtoList(List<Compilation> compilations);
}
