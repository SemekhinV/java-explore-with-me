package ru.practicum.ewm.compilation.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

@Service
@Transactional
@AllArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository repository;

    private final CompilationMapper mapper;

    private final EventRepository eventRepository;

    @Override
    public CompilationDto save(NewCompilationDto dto) {

        var events = eventRepository.findAllByIdIn(dto.getEvents());

        var compilation = mapper.toEntity(dto, events);

        compilation.setEvents(events);

        var response = repository.save(compilation);

        return mapper.toDto(response);
    }

    @Override
    public void delete(Long compId) {

        repository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException("Подборки с id = " + compId + " не существует"));

        repository.deleteById(compId);
    }

    @Override
    public CompilationDto update(UpdateCompilationRequest dto) {

        var compilation = repository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Подборки с id = " + dto.getId() + " не существует"));

        if (dto.getTitle() != null) {

            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {

            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {

            var events = eventRepository.findAllByIdIn(dto.getEvents());

            compilation.setEvents(events);
        }

        var response = repository.save(compilation);

        return mapper.toDto(response);
    }
}
