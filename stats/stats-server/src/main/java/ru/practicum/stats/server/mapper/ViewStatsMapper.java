package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto mapToDto(ViewStats stats);
}
