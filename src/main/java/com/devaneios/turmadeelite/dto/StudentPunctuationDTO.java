package com.devaneios.turmadeelite.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentPunctuationDTO {
    private String studentName;
    private Double punctuation;
}
