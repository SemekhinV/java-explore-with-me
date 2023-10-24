package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class UserController {

    private final UserService service;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRequestDto save(@Valid @RequestBody UserRequestDto user) {

        return service.save(user);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserRequestDto> get(@RequestParam(required = false) List<Long> idIn,
                                    @RequestParam(defaultValue = "20") Integer size,
                                    @RequestParam(defaultValue = "0") Integer from) {

        return service.get(idIn, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }
}
