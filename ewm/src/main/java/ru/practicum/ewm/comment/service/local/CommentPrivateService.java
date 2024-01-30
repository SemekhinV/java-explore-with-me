package ru.practicum.ewm.comment.service.local;

import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentPrivateService {

    CommentResponseDto save(NewCommentDto newCommentDto, Long eventId, Long userId);

    CommentResponseDto update(NewCommentDto commentDto);

    void delete(Long commentId, Long userId);

    List<CommentResponseDto> getAllUserCommentsToEvent(Long userId, Long eventId);
}
