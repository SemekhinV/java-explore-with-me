package ru.practicum.ewm.event.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.EventStateException;
import ru.practicum.ewm.event.dto.AdminDtoWithParameters;
import ru.practicum.ewm.event.dto.UserDtoWithParameters;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortState;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class EvenRepositoryImpl implements EventRepository {

    private final EntityManager entityManager;

    @Override
    public List<Event> admin(AdminDtoWithParameters dto, LocalDateTime start, LocalDateTime end) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> query = builder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);

        Predicate criteria = builder.conjunction();

        if (dto.getRangeStart() != null) {

            criteria = builder.and(criteria,
                    builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    start));
        }

        if (dto.getRangeEnd() != null) {

            criteria = builder.and(criteria,
                    builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    end));
        }

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {

            criteria = builder.and(criteria, root.get("category").in(dto.getCategories()));
        }

        if (dto.getUsers() != null && !dto.getUsers().isEmpty()) {

            criteria = builder.and(criteria, root.get("initiator").in(dto.getUsers()));
        }

        if (dto.getStates() != null) {

            criteria = builder.and(criteria, root.get("state").in(dto.getStates()));
        }

        query.select(root).where(criteria);

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(dto.getFrom())
                .setMaxResults(dto.getSize())
                .getResultList();

        if (events.size() == 0) {

            return new ArrayList<>();
        }

        return events;
    }

    @Override
    public List<Event> user(UserDtoWithParameters dto, LocalDateTime start, LocalDateTime end) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> query = builder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);

        Predicate criteria = builder.conjunction();

        if (dto.getText() != null) {

            criteria = builder.and(criteria, builder.or(
                    builder.like(builder.lower(root.get("annotation")), "%" + dto.getText().toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("description")), "%" + dto.getText().toLowerCase() + "%")));
        }

        if (dto.getCategories() != null && dto.getCategories().size() > 0) {

            criteria = builder.and(criteria, root.get("category").in(dto.getCategories()));
        }

        if (dto.getPaid() != null) {

            Predicate predicate;

            if (dto.getPaid()) predicate = builder.isTrue(root.get("paid"));

            else predicate = builder.isFalse(root.get("paid"));

            criteria = builder.and(criteria, predicate);
        }

        if (end != null) {

            criteria = builder.and(criteria,
                    builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                   end));
        }

        if (start != null) {

            criteria = builder.and(criteria,
                    builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    start));
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(dto.getFrom())
                .setMaxResults(dto.getSize())
                .getResultList();

        if (events.stream().anyMatch(
                (event) -> !EventState.PUBLISHED.equals(event.getState()))) {

            throw new EventStateException("Event must be published.");
        }

        if (dto.getAvailable()) {

            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (dto.getSort() != null) {

            if (SortState.EVENT_DATE.equals(dto.getSort())) {

                events = events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            } else {

                events = events.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .collect(Collectors.toList());
            }
        }

        return events.size() == 0 ?
                new ArrayList<>() :
                events;
    }
}
