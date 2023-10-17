package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.server.entity.Hit;

public interface StatsRepository extends JpaRepository<Hit,Long>, HitRepository {

}
