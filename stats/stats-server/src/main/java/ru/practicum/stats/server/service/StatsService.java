package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitDto saveHit(HitDto hitDto);

    List<ViewStatsDto> getHits(LocalDateTime start,
                               LocalDateTime end,
                               List<String> uri,
                               boolean unique);

}
