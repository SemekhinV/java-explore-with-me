package ru.practicum.ewm.category.service.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryUserServiceImpl implements CategoryUserService {

    private final CategoryRepository repository;

    private final CategoryMapper mapper;

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {

        return repository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto getCategoryById(Long catId) {

        var category = repository.findById(catId).orElseThrow(
                () -> new EntityNotFoundException("Категории с ID " + catId + " не существует."));

        return mapper.toDto(category);
    }
}
