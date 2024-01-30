package ru.practicum.ewm.comment.service.user;

import ru.practicum.ewm.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentUserService {

    CommentResponseDto getCommentById(Long id);

    List<CommentResponseDto> getCommentsByEventId(Long eventId);
}
