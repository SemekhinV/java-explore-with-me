package ru.practicum.ewm.comment.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentUserServiceImpl implements CommentUserService {

    private final CommentRepository repository;

    private final CommentMapper mapper;

    private final EventRepository eventRepository;

    @Override
    public List<CommentResponseDto> getCommentsByEventId(Long eventId) {

        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        return mapper.toDtoList(repository.findAllByEventId(eventId));
    }

    @Override
    public CommentResponseDto getCommentById(Long commentId) {

        return mapper.toDto(repository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий с id = " + commentId + " не найден.")));
    }
}
