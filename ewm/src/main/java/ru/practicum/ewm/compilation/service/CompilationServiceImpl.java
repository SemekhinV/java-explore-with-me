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
import ru.practicum.ewm.error.exception.EntityExistException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationJpaRepository repository;

    private final CompilationMapper mapper;
    private final EventJpaRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto save(CompilationRequestDto savedCompilationDto) {

        Optional<Event> events = eventRepository.findAllByIdIn(savedCompilationDto.getEvents());

        Compilation compilation = Compilation.builder()
                .pinned(savedCompilationDto.getPinned())
                .title(savedCompilationDto.getTitle())
                .events(events.map(Set::of).orElseGet(Set::of))
                .build();

        Compilation saved = repository.save(compilation);

        return mapper.toDto(saved);
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

        return mapper.toDtoList(repository.getByPinned(pinned, from, size));
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

            Optional<Event> events = eventRepository.findAllByIdIn(dto.getEvents());

            compilation.setEvents(events.map(Set::of).orElseGet(Set::of));
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
