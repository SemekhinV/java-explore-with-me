package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {

        var users = ids == null || ids.isEmpty() ?
                repository.findAll(PageRequest.of(from / size, size)).toList()
                : repository.findAllById(ids);

        return mapper.toDtoList(users);
    }

    @Override
    @Transactional
    public UserDto save(NewUserRequest user) {

        if (repository.existsUserByName(user.getName())) {

            throw new EntityConflictException("Пользователь с именем " + user.getName() + " уже существует.");
        }

        var response = repository.save(mapper.toUser(user));

        return mapper.toUserDto(response);
    }

    @Override
    public void delete(Long id) {

        if (repository.existsById(id)) {

            repository.deleteById(id);

            return;
        }

        throw new EntityNotFoundException("Ошибка удаления. Пользователь с id " + id  + " не найден.");
    }
}
