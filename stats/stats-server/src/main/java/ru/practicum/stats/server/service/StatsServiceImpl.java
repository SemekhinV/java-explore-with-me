package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.server.mapper.HitMapper;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.model.ViewStats;
import ru.practicum.stats.server.repository.StatsRepository;
import ru.practicum.stats.dto.*;
import ru.practicum.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    private final StatsRepository statsRepository;

    private final ViewStatsMapper viewMapper;

    private final HitMapper hitMapper;

    @Override
    public HitDto saveHit(HitDto hitDto) {

        return hitMapper.toDto(hitRepository.save(hitMapper.toEntity(hitDto)));
    }

    @Override
    public List<ViewStatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {

        List<ViewStats> stats;

        stats = unique ? statsRepository.getUniqueStats(start, end, uri) : statsRepository.getStats(start, end, uri);

        return viewMapper.toDto(stats);
    }

}

