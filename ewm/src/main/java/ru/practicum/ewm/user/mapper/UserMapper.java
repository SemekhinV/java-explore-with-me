package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRequestDto user);

    UserRequestDto toUserDto(User user);

    List<UserRequestDto> toDtoList(List<User> users);
}
