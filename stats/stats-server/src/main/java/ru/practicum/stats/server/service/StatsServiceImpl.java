package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.ViewStats;
import ru.practicum.stats.server.mapper.HitMapper;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    private final ViewStatsMapper viewMapper;

    private final HitMapper hitMapper;

    @Override
    public HitDto saveHit(HitDto hitDto) {

        return hitMapper.toDto(repository.save(hitMapper.toEntity(hitDto)));
    }

    @Override
    public List<ViewStatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {

        List<ViewStats> stats;

        stats = unique ? repository.getStats(start, end, uri) : repository.getUniqueStats(start, end, uri);

        return stats.stream()
                .map(viewMapper::toDto)
                .collect(Collectors.toList());
    }
}

