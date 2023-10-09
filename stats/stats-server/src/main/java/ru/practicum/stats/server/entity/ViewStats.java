package ru.practicum.stats.server.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStats {

    String app;

    String uri;

    Long hits;
}


