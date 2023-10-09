package ru.practicum.stats.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.server.entity.Hit;
import ru.practicum.stats.server.entity.ViewStats;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitRepositoryImpl implements HitRepository {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ViewStats> criteriaQuery = criteriaBuilder.createQuery(ViewStats.class);

        Root<Hit> statsRoot = criteriaQuery.from(Hit.class);

        List<Predicate> predicateList = new ArrayList<>();

        criteriaQuery.select(criteriaBuilder.construct(
                ViewStats.class,
                statsRoot.get("uri"),
                statsRoot.get("app"),
                unique
                        ? criteriaBuilder.countDistinct(statsRoot.get("ip"))
                        : criteriaBuilder.count(statsRoot.get("ip"))));

        criteriaQuery.groupBy(
                statsRoot.get("app"),
                statsRoot.get("uri"),
                statsRoot.get("ip"));

        criteriaQuery.orderBy(criteriaBuilder.desc(
                unique
                        ? criteriaBuilder.countDistinct(statsRoot.get("ip"))
                        : criteriaBuilder.count(statsRoot.get("ip"))));

        predicateList.add(criteriaBuilder.between(statsRoot.get("timestamp"), start, end));

        if (uris != null) {

            predicateList.add(criteriaBuilder.and(statsRoot.get("uri").in(uris)));
        }

        criteriaQuery.where(predicateList.toArray(Predicate[]::new));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
