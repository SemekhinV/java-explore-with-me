package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.user.CategoryUserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.CATEGORY_REQUEST;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryUserController {

    private final CategoryUserService service;

    @GetMapping
    private List<CategoryDto> getCategories(@RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info(CATEGORY_REQUEST, "get with size restricted");

        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable Long catId) {

        log.info(CATEGORY_REQUEST, "get by id");

        return service.getCategoryById(catId);
    }
}
