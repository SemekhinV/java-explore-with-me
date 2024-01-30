package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.EVENT_REQUEST;
import static ru.practicum.ewm.util.EwmPatterns.USER_REQUEST;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(value = "/admin/users")
public class UserController {

    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get(@RequestParam(required = false) List<Long> ids,
                             @RequestParam(defaultValue = "0") Integer from,
                             @RequestParam(defaultValue = "10") Integer size) {

        log.info(USER_REQUEST, "get with size restricted and id in");

        return service.getAllUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@Valid @RequestBody NewUserRequest user) {

        log.info(EVENT_REQUEST, "create");

        return service.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        log.info(EVENT_REQUEST, "delete");

        service.delete(id);
    }
}
