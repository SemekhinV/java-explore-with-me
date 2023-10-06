package ru.practicum.stats.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app", length = 15, nullable = false)
    String app;

    @Column(name = "uri", length = 15, nullable = false)
    String uri;

    @Column(name = "hits", nullable = false)
    Long hits;
}
