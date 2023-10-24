package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EntityExistException;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    @Override
    public UserRequestDto save(UserRequestDto user) {

        if (repository.existsUserByName(user.getName())) {
            throw new EntityExistException("User " + user.getName() + "already exist.");
        }

        return mapper.toUserDto(
                repository.save(
                        mapper.toUser(user)));
    }

    @Override
    public List<UserRequestDto> get(List<Long> idIn, int from, int size) {

        return idIn.isEmpty() ?
                mapper.toDtoList(repository.findAll(PageRequest.of(from / size, size)).toList()) :
                mapper.toDtoList(repository.findAllById(idIn));
    }

    @Override
    public void delete(Long id) {

        repository.deleteById(id);
    }
}
