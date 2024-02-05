package ru.practicum.ewm.comment.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.error.exception.EntityNotFoundException;

@Service
@AllArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {

    private final CommentRepository repository;

    @Override
    public void delete(Long commentId) {

        if (repository.existsCommentById(commentId)) {

            repository.deleteById(commentId);

            return;
        }

        throw new EntityNotFoundException("Комментарий с id = " + commentId + " не найден.");
    }
}
