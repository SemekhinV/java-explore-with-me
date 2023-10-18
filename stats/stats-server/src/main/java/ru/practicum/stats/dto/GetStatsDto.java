package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetStatsDto {

    LocalDateTime start;

    LocalDateTime end;

    Boolean unique;

    List<String> uris;
}
