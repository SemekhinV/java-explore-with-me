package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.dto.GetStatsDto;
import ru.practicum.stats.server.mapper.HitMapper;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    private final ViewStatsMapper viewMapper;

    private final HitMapper hitMapper;

    @Override
    @Transactional
    public void saveHit(HitDto hitDto) {

        repository.save(hitMapper.mapToEntity(hitDto));
    }

    @Override
    public List<ViewStatsDto> getHits(GetStatsDto dto) {

        System.out.println();

        return repository.getStats(dto.getStart(), dto.getEnd(), dto.getUris(), dto.getUnique())
                .stream()
                .map(viewMapper::mapToDto)
                .collect(Collectors.toList());
    }
}

