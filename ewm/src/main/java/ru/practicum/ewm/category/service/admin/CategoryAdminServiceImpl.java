package ru.practicum.ewm.category.service.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository repository;

    private final CategoryMapper mapper;

    private final EventRepository eventRepository;

    @Override
    public CategoryDto save(NewCategoryDto dto) {

        if (repository.existsCategoryByName(dto.getName())) {

            throw new EntityConflictException("Категория с названием " + dto.getName() + " уже существует.");
        }

        var response = repository.save(mapper.toEntity(dto));

        return mapper.toDto(response);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {

        var category = repository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Категории с ID = " + dto.getId() + " не существует"));

        if (category.getName().equals(dto.getName())) {

            return mapper.toDto(category);
        }

        if (repository.existsCategoryByNameAndIdNot(dto.getName(), dto.getId())) {

            throw new EntityConflictException("Категория с названием " + dto.getName() + " уже существует.");
        }

        category.setName(dto.getName());

        return mapper.toDto(repository.save(category));
    }

    @Override
    public void delete(Long catId) {

        repository.findById(catId).orElseThrow(
                () -> new EntityNotFoundException("Категории с ID = " + catId + " не существует"));

        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {

            throw new EntityConflictException("Категория не может быть удалена, т.к. к ней привязаны события.");
        }

        repository.deleteById(catId);
    }
}
