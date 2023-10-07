package ru.practicum.stats.server.repository;

import ru.practicum.stats.server.entity.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository {

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
