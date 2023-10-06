package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.dto.HitDto;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit toEntity(HitDto dto);

    HitDto toDto(Hit hit);
}
