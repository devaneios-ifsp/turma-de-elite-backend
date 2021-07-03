package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.School;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SchoolCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String identifier;
    @NotNull
    private Boolean isActive;

    public School toEntity() {
        return School
                .builder()
                .name(this.name)
                .identifier(this.identifier)
                .isActive(this.isActive)
                .build();
    }
}
