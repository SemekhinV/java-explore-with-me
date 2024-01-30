package ru.practicum.ewm.category.service.admin;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

public interface CategoryAdminService {

    CategoryDto save(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long catId);
}
