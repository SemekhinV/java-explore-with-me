package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.dto.ConfirmedRequests;
import ru.practicum.ewm.request.entity.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequesterAndId(Long userId, Long requestId);

    List<Request> findAllByRequester(Long userId);

    List<Request> findAllByEvent(Long eventId);

    Boolean existsByRequesterAndEvent(Long userId, Long eventId);

    @Query("SELECT request FROM Request AS request " +
            "JOIN Event AS event ON request.event = event.id " +
            "WHERE request.event = :eventId AND event.initiator.id = :userId")
    List<Request> findAllByEventWithInitiator(@Param("userId") Long userId, @Param("eventId") Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    @Query("SELECT new ru.practicum.ewm.request.dto.ConfirmedRequests(r.event, COUNT(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.event IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event")
    List<ConfirmedRequests> getConfirmedRequestsCount(List<Long> eventIds);

    @Query("SELECT COUNT(r.id) " +
            "FROM Request AS r " +
            "WHERE r.event = ?1 " +
            "AND r.status = 'CONFIRMED' ")
    Long getEventConfirmedRequestCount(Long eventId);
}
