package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.ViewStats;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStats toEntity(ViewStatsDto dto);

    ViewStatsDto toDto(ViewStats stats);

    List<ViewStatsDto> toDto(List<ViewStats> stats);
}
