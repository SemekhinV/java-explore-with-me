package ru.practicum.ewm.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.exception.BadInputParametersException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventSearchingProvider {

    @PersistenceContext
    private final EntityManager manager;

    public List<Event> getAdminFilters(List<Long> users,
                                       List<EventState> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from,
                                       Integer size) {

        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {

            throw new BadInputParametersException("Время окончания не может быть раньше времени начала.");
        }

        var query = mapSearchingFilters(manager, users, states, categories, rangeStart, rangeEnd,
                null, null);

        return manager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }


    public List<Event> getUserFilters(Integer size,
                                      Integer from,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean paid,
                                      String text) {

        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {

            throw new BadInputParametersException("Время окончания не может быть раньше времени начала.");
        }

        var query = mapSearchingFilters(manager, null, null, categories, rangeStart, rangeEnd, text, paid);

        return manager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    private CriteriaQuery<Event> mapSearchingFilters(EntityManager manager,
                                          List<Long> users,
                                          List<EventState> states,
                                          List<Long> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          String text,
                                          Boolean paid) {

        var builder = manager.getCriteriaBuilder();

        var query = builder.createQuery(Event.class);

        var root = query.from(Event.class);

        var predicate = builder.conjunction();

        if (Objects.nonNull(rangeStart)) {

            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), rangeStart));
        }

        if (Objects.nonNull(rangeEnd)) {

            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), rangeEnd));
        }

        if (Objects.nonNull(paid)) {

            predicate = paid ?
                    builder.and(predicate, builder.isTrue(root.get("paid")))
                    : builder.and(predicate, builder.isFalse(root.get("paid")));
        }

        if (Objects.nonNull(users)) {

            builder.and(predicate, builder.equal(root.get("initiator"), users));
        }

        if (Objects.nonNull(text)) {

            predicate = builder.and(predicate, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (Objects.nonNull(categories) && !categories.isEmpty()) {

            predicate = builder.and(predicate, root.get("category").in(categories));
        }

        if (Objects.nonNull(states) && states.isEmpty()) {

            predicate = builder.and(predicate, root.get("state").in(states));
        }

        query.select(root).where(predicate);

        return query;
    }
}
