package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.UserExistException;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    @Override
    @Transactional
    public UserRequestDto save(UserRequestDto user) {

        if (repository.existsUserByName(user.getName())) {

            log.error("User " + user.getName() + "already exist.");

            throw new UserExistException("User " + user.getName() + "already exist.");
        }

        return mapper.toUserDto(
                repository.save(
                        mapper.toUser(user)));
    }

    @Override
    public List<UserRequestDto> get(List<Long> ids, int from, int size) {

        if (ids != null && ids.size() > 0) {

            return mapper.toDtoList(repository.findAllById(ids));
        } else {

            return mapper.toDtoList(repository.findAll(PageRequest.of(from / size, size)).toList());

        }
    }

    @Override
    @Transactional
    public void delete(Long id) {

        repository.deleteById(id);
    }
}
