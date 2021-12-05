package com.devaneios.turmadeelite.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ActivityByTeacherDTO {
    private int activity;
    private String teacher;
}
