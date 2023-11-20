package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto save(CategoryRequestDto category);

    CategoryResponseDto update(Long id, CategoryRequestDto category);

    void delete(Long id);

    CategoryResponseDto get(Long id);

    List<CategoryResponseDto> get(int from, int size);
}
