package ru.practicum.ewm.category.service.user;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryUserService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
