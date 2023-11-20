package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    List<Event> findAllByIdIn(List<Long> idIn);

    Boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndPublishedOnIsNotNull(Long id);
}
