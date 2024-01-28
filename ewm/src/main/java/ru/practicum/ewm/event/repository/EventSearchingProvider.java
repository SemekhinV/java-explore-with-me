package ru.practicum.ewm.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventAdminFilters;
import ru.practicum.ewm.event.dto.EventPublicFilters;
import ru.practicum.ewm.event.entity.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventSearchingProvider {

    @PersistenceContext
    private final EntityManager manager;

    public List<Event> getAdminFilters(EventAdminFilters filters) {

        var query = mapSearchingFilters(null, filters);

        return manager.createQuery(query).setFirstResult(filters.getFrom()).setMaxResults(filters.getSize())
                .getResultList();
    }


    public List<Event> getUserFilters(EventPublicFilters filters) {

        var query = mapSearchingFilters(filters, null);

        return manager.createQuery(query).setFirstResult(filters.getFrom()).setMaxResults(filters.getSize())
                .getResultList();
    }

    private CriteriaQuery<Event> mapSearchingFilters(EventPublicFilters publicFilters, EventAdminFilters adminFilters) {

        var builder = manager.getCriteriaBuilder();

        var query = builder.createQuery(Event.class);

        var root = query.from(Event.class);

        var predicate = builder.conjunction();

        if (publicFilters != null) {

            predicate = mapPublicFilters(publicFilters, predicate, builder, root);
        }

        if (adminFilters != null) {

            predicate = mapAdminFilters(adminFilters, predicate, builder, root);
        }

        query.select(root).where(predicate);

        return query;
    }

    private Predicate mapPublicFilters(EventPublicFilters filters, Predicate predicate, CriteriaBuilder builder,
                                       Root<Event> root) {

        if (Objects.nonNull(filters.getRangeStart())) {

            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), filters.getRangeStart()));
        }

        if (Objects.nonNull(filters.getRangeEnd())) {

            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), filters.getRangeEnd()));
        }

        if (Objects.nonNull(filters.getPaid())) {

            predicate = filters.getPaid() ?
                    builder.and(predicate, builder.isTrue(root.get("paid")))
                    : builder.and(predicate, builder.isFalse(root.get("paid")));
        }

        if (Objects.nonNull(filters.getText())) {

            predicate = builder.and(predicate, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + filters.getText().toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + filters.getText().toLowerCase() + "%")));
        }

        if (Objects.nonNull(filters.getCategories()) && !filters.getCategories().isEmpty()) {

            predicate = builder.and(predicate, root.get("category").in(filters.getCategories()));

        }

        return predicate;
    }

    private Predicate mapAdminFilters(EventAdminFilters filters, Predicate predicate, CriteriaBuilder builder,
                                      Root<Event> root) {

        if (Objects.nonNull(filters.getRangeStart())) {

            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), filters.getRangeStart()));
        }

        if (Objects.nonNull(filters.getRangeEnd())) {

            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("eventDate")
                    .as(LocalDateTime.class), filters.getRangeEnd()));
        }

        if (Objects.nonNull(filters.getUsers())) {

            builder.and(predicate, builder.equal(root.get("initiator"), filters.getUsers()));
        }


        if (Objects.nonNull(filters.getCategories()) && !filters.getCategories().isEmpty()) {

            predicate = builder.and(predicate, root.get("category").in(filters.getCategories()));
        }

        if (Objects.nonNull(filters.getStates()) && filters.getStates().isEmpty()) {

            predicate = builder.and(predicate, root.get("state").in(filters.getStates()));
        }

        return predicate;
    }
}
