package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    private List<CategoryResponseDto> get(@RequestParam(defaultValue = "10", required = false) int size,
                                          @RequestParam(defaultValue = "0", required = false) int from) {

        return service.get(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto get(@PathVariable Long catId) {

        return service.get(catId);
    }
}
