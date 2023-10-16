package ru.practicum.stats.server.repository;

import lombok.RequiredArgsConstructor;
import ru.practicum.stats.server.entity.Hit;
import ru.practicum.stats.server.entity.ViewStats;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HitRepositoryImpl implements HitRepository {

    private final EntityManager entityManager;


    @Override
    @Transactional
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ViewStats> query = builder.createQuery(ViewStats.class);

        Root<Hit> root = query.from(Hit.class);

        List<Predicate> predicateList = new ArrayList<>();

        query.select(builder.construct(
                ViewStats.class,
                root.get("uri"),
                root.get("app"),
                unique
                        ? builder.countDistinct(root.get("ip"))
                        : builder.count(root.get("ip"))));

        query.groupBy(
                root.get("app"),
                root.get("uri"));

        query.orderBy(builder.desc(
                unique
                        ? builder.countDistinct(root.get("ip"))
                        : builder.count(root.get("ip"))));

        predicateList.add(builder.between(root.get("timestamp"), start, end));

        if (uris != null) {

            predicateList.add(builder.and(root.get("uri").in(uris)));
        }

        query.where(predicateList.toArray(Predicate[]::new));

        return entityManager.createQuery(query).getResultList();
    }

}
