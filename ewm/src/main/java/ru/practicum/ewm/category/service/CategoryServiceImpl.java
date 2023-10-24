package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.CategoryNotEmpyException;
import ru.practicum.ewm.error.exception.EntityExistException;
import ru.practicum.ewm.event.repository.EventJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    private final EventJpaRepository eventRepository;

    @Override
    @Transactional
    public CategoryResponseDto save(CategoryRequestDto category) {

        if (repository.existsCategoryByName(category.getName())) {
            throw new EntityExistException("Category " + category.getName() + " already exist.");
        }

        return mapper.toResponseDto(
                repository.save(
                        mapper.toCategory(category)));
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long id, CategoryResponseDto category) {

        if (repository.existsCategoryByName(category.getName())) {
            throw new EntityExistException("the category with the passed parameters already exists. Update error");
        }

        Category fromDb = repository.findById(id).orElseThrow(
                () -> new EntityExistException("Category with id=" + id + " does`t exist."));

        fromDb.setName(category.getName());

        return mapper.toResponseDto(repository.save(fromDb));
    }

    @Override
    public void delete(Long id) {

        if (eventRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmpyException("Category with id=" + id + " isn`t empty.");
        }

        repository.deleteById(id);
    }

    @Override
    public CategoryResponseDto get(Long id) {

        Category category = repository.findById(id).orElseThrow(
                () -> new EntityExistException("Category with id=" + id + " does`t exist.")
        );

        return mapper.toResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> get(int from, int size) {

        return mapper.toDtoList(repository.findAll(PageRequest.of(from / size, size)).toList());
    }
}
