package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.entity.Compilation;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationJpaRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.util.EntityExistException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationJpaRepository repository;

    private final CompilationRepository simpleCompRepository;

    private final CompilationMapper mapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto save(CompilationRequestDto dto) {

        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());

        Compilation compilation = Compilation.builder()
                .pinned(dto.getPinned() != null && dto.getPinned())
                .title(dto.getTitle())
                .events(new HashSet<>(events))
                .build();

        return mapper.toDto(repository.save(compilation));
    }

    @Override
    public CompilationDto get(Long compId) {

        Compilation compilation = repository.findById(compId).orElseThrow(
                () -> {
                    log.error("Compilation does not exist");
                    throw new EntityExistException("Compilation does not exist");
                });

        return mapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> get(Boolean pinned, Integer from, Integer size) {

        return mapper.toDtoList(simpleCompRepository.getPinned(pinned, from, size));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, CompilationUpdateDto dto) {

        Compilation compilation = repository.findById(compId).orElseThrow(
                () -> {
                    log.error("Compilation does not exist");
                    throw new EntityExistException("Compilation does not exist");
                });

        List<Long> eventsIds = dto.getEvents();

        if (eventsIds != null) {

            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());

            compilation.setEvents(new HashSet<>(events));
        }

        compilation.toBuilder()
                .title(dto.getTitle() == null ? compilation.getTitle() : dto.getTitle())
                .pinned(dto.getPinned() == null ? compilation.getPinned() : dto.getPinned())
                .build();

        return mapper.toDto(repository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compId) {

        repository.deleteById(compId);
    }
}
