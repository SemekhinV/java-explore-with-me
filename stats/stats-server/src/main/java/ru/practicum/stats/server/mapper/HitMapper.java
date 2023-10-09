package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.entity.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit toEntity(HitDto dto);

    HitDto toDto(Hit hit);
}
