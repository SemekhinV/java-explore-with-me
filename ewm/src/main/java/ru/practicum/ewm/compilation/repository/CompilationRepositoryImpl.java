package ru.practicum.ewm.compilation.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.entity.Compilation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
@AllArgsConstructor
public class CompilationRepositoryImpl implements CompilationRepository {

    private final EntityManager entityManager;

    @Override
    public List<Compilation> getPinned(Boolean pinned, Integer from, Integer size) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Compilation> query = builder.createQuery(Compilation.class);

        Root<Compilation> root = query.from(Compilation.class);

        Predicate criteria = builder.conjunction();

        Predicate isPinned;

        if (pinned != null) {

            if (pinned) {

                isPinned = builder.isTrue(root.get("pinned"));
            } else {

                isPinned = builder.isFalse(root.get("pinned"));
            }

            criteria = builder.and(criteria, isPinned);
        }

        query.select(root).where(criteria);

        return entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}
