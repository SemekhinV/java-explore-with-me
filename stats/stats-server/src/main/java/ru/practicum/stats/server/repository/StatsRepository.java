package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<ViewStats, Long> {


    @Query(value = ""
            + "SELECT NEW ViewStats(hit.uri, hit.app, COUNT(hit.ip)) "
            + "FROM Hit hit "
            + "WHERE hit.uri IN ?3 AND hit.timestamp BETWEEN ?1 AND ?2 "
            + "GROUP BY hit.uri, hit.app "
            + "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStats> getStats(LocalDateTime start,
                            LocalDateTime end,
                            List<String> uris);

    @Query(value = ""
            + "SELECT NEW ViewStats(hit.uri, hit.app, COUNT(DISTINCT hit.ip)) "
            + "FROM Hit hit "
            + "WHERE hit.uri IN ?3 AND hit.timestamp BETWEEN ?1 AND ?2 "
            + "GROUP BY hit.uri, hit.app "
            + "ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<ViewStats> getUniqueStats(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris);

}
