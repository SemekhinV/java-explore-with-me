package ru.practicum.ewm.event.repository;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.GetWithParametersDto;
import ru.practicum.ewm.event.entity.Event;
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

@AllArgsConstructor
@Transactional(readOnly = true)
public class EvenRepositoryImpl implements EventRepository {

    private final EntityManager entityManager;

    /**
     * Get method with parameters to get the list of events.
     * Access level - USER
     * The name of the method was chosen that way because otherwise an error with the Jpa repository occurred
     *
     * @param dto A special class for a get request with parameters for both user and administrator.
     *
     * @return List of events based on passed parameters
     */
    @Override
    public List<Event> getByInitiator(GetWithParametersDto dto) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> query = builder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);

        Predicate criteria = builder.conjunction();


        if (dto.getRangeStart() != null) {

            criteria = builder.and(criteria,
                    builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    dto.getStartDate()));
        }

        if (dto.getRangeEnd() != null) {

            criteria = builder.and(criteria,
                    builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    dto.getEndDate()));
        }

        if (dto.getCategories() != null && dto.getCategories().size() > 0) {

            criteria = builder.and(criteria, root.get("category").in(dto.getCategories()));
        }

        if (dto.getUsers() != null && dto.getUsers().size() > 0) {

            criteria = builder.and(criteria, root.get("initiator").in(dto.getUsers()));
        }

        if (dto.getStates() != null) {

            criteria = builder.and(criteria, root.get("state").in(dto.getStates()));
        }

        query.select(root).where(criteria);

        var events = entityManager.createQuery(query)
                .setFirstResult(dto.getFrom())
                .setMaxResults(dto.getSize())
                .getResultList();

        if (events.size() == 0) return new ArrayList<>();

        return events;
    }

    /**
     * Get method with parameters to get the list of events.
     * Access level - ADMIN.
     * The name of the method was chosen that way because otherwise an error with the Jpa repository occurred
     *
     * @param dto A special class for a get request with parameters for both user and administrator.
     *
     * @return List of events based on passed parameters
     */
    @Override
    public List<Event> getByCategory(GetWithParametersDto dto) {

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

        if (dto.getEndDate() != null) {

            criteria = builder.and(criteria,
                    builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    dto.getEndDate()));
        }

        if (dto.getStartDate() != null) {

            criteria = builder.and(criteria,
                    builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    dto.getStartDate()));
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(dto.getFrom())
                .setMaxResults(dto.getSize())
                .getResultList();

        if (dto.getAvailable()) {

            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < (long) event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (dto.getSort() != null) {

            if (SortState.EVENT_DATE.equals(dto.getSort())) {

                events = events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            }

            else {

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
