package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto save(@Valid @RequestBody CategoryRequestDto dto) {

        return service.save(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/categories/{catId}")
    public CategoryResponseDto update(@Valid @RequestBody CategoryResponseDto dto,
                                      @PathVariable Long catId) {

        return service.update(catId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/categories/{catId}")
    public void delete(@PathVariable Long catId) {

        service.delete(catId);
    }
}
