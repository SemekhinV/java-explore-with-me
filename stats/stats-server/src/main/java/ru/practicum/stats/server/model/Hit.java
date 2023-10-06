package ru.practicum.stats.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Hit")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ip", length = 15, nullable = false)
    String ip;

    @Column(name = "uri", length = 140, nullable = false)
    String uri;

    @Column(name = "app", length = 140, nullable = false)
    String app;

    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;
}
