package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.entity.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponseDto toDto(Comment comment);

    List<CommentResponseDto> toDtoList(List<Comment> comments);
}
