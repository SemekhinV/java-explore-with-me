package ru.practicum.stats.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
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
