package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequestDto data);

    CategoryResponseDto toResponseDto(Category data);

    List<CategoryResponseDto> toDtoList(List<Category> categoryList);
}
