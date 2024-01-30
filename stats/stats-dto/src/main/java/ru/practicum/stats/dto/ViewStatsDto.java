package ru.practicum.stats.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {

    private String app;

    private String uri;

    private Long hits;
}