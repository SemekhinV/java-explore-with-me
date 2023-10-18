package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.GetStatsDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void saveHit(HitDto hitDto);

    List<ViewStatsDto> getHits(GetStatsDto dto);

}
