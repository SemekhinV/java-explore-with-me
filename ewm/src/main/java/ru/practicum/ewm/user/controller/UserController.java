package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(value = "/admin/users")
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRequestDto save(@Valid @RequestBody UserRequestDto user) {

        System.out.println("DDDDDDDDs");

        return service.save(user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserRequestDto> get(@RequestParam(required = false) List<Long> idIn,
                                    @RequestParam(defaultValue = "20") Integer size,
                                    @RequestParam(defaultValue = "0") Integer from) {

        return service.get(idIn, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }
}
