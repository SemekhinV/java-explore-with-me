package ru.practicum.ewm.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsCommentById(Long commentId);

    List<Comment> findAllByEventId(Long eventId);

    List<Comment> findAllByEventIdAndUserIdOrderByIdAsc(Long eventId, Long userId);
}
