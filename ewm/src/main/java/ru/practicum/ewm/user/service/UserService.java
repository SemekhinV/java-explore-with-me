package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {

    UserRequestDto save(UserRequestDto user);

    List<UserRequestDto> get(List<Long> idIn, int from, int size);

    void delete(Long id);
}
