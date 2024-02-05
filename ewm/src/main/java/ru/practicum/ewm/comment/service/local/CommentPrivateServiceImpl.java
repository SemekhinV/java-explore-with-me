package ru.practicum.ewm.comment.service.local;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.entity.Comment;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.error.exception.EntityConflictException;
import ru.practicum.ewm.error.exception.EntityNotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.event.enums.EventState.PUBLISHED;

@Service
@Transactional
@AllArgsConstructor
public class CommentPrivateServiceImpl implements CommentPrivateService {

    private final CommentRepository repository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CommentMapper mapper;

    @Override
    public CommentResponseDto save(NewCommentDto newCommentDto, Long eventId, Long userId) {

        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        if (!event.getInitiator().equals(user)) {

            throw new EntityNotFoundException("Выбранное событие не принадлежит указанному пользователю.");
        }

        if (!event.getState().equals(PUBLISHED)) {

            throw new EntityConflictException("Невозможно добавить комментарий к неопубликованному событию.");
        }

        var comment = new Comment(null, event, user, newCommentDto.getText(), LocalDateTime.now(), null);

        return mapper.toDto(repository.save(comment));
    }

    @Override
    public CommentResponseDto update(NewCommentDto commentDto) {

        var comment = checkUserAndCommentExist(commentDto.getUserId(), commentDto.getId());

        if (!commentDto.getText().isEmpty()) {

            comment.setText(commentDto.getText());

            comment.setEdited(LocalDateTime.now());
        }

        return mapper.toDto(repository.save(comment));
    }

    @Override
    public void delete(Long commentId, Long userId, Long eventId) {

        checkUserAndCommentExist(userId, commentId);

        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        repository.deleteById(commentId);
    }

    @Override
    public List<CommentResponseDto> getAllUserCommentsToEvent(Long userId, Long eventId) {

        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id=" + eventId + " не существует"));

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        var comments = repository.findAllByEventIdAndUserIdOrderByIdAsc(eventId, userId);

        return mapper.toDtoList(comments);
    }

    private Comment checkUserAndCommentExist(Long userId, Long commentId) {

        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId  + " не найден."));

        var comment = repository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий с id = " + commentId + " не найден."));

        if (!comment.getUser().equals(user)) {

            throw new EntityConflictException("Выбранный комментарий не принадлежит указанному пользователю.");
        }

        return comment;
    }
}
