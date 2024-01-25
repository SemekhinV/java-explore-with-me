package ru.practicum.ewm.compilation.service.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.EntityNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationUserServiceImpl implements CompilationUserService {

    private final CompilationRepository repository;

    private final CompilationMapper mapper;

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {

        var compilations = pinned != null ?
                repository.findAllByPinned(pinned, PageRequest.of(from / size, size))
                : repository.findAll(PageRequest.of(from / size, size)).toList();

        return mapper.toDtoList(compilations);
    }

    @Override
    @Transactional
    public CompilationDto getCompilationById(Long compId) {

        var compilation = repository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException("Подборка событий с id = " + compId + " не существует"));

        return mapper.toDto(compilation);
    }
}
