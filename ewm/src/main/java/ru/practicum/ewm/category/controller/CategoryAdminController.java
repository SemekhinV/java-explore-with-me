package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.admin.CategoryAdminService;

import javax.validation.Valid;

import static ru.practicum.ewm.util.EwmPatterns.CATEGORY_REQUEST;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/categories")
public class CategoryAdminController {

    private final CategoryAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@Valid @RequestBody NewCategoryDto dto) {

        log.info(CATEGORY_REQUEST, "create");

        return service.save(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Valid @RequestBody CategoryDto dto, @PathVariable Long catId) {

        log.info(CATEGORY_REQUEST, "update");

        dto.setId(catId);

        return service.update(dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {

        log.info(CATEGORY_REQUEST, "delete");

        service.delete(catId);
    }
}
