package ru.practicum.stats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
public class HitDto {

    private Long id;

    @NotBlank
    private String ip;

    @NotBlank
    private String uri;

    @NotBlank
    private String app;

    private String timestamp;
}
