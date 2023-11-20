package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.entity.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    RequestDto toDto(Request request);

    List<RequestDto> toDtoList(List<Request> requests);
}
