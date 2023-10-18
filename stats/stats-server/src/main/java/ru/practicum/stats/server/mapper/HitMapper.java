package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.entity.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit mapToEntity(HitDto dto);
}
