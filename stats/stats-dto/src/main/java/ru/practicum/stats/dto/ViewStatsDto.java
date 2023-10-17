package ru.practicum.stats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ViewStatsDto {

    private String app;

    private String uri;

    private Long hits;
}