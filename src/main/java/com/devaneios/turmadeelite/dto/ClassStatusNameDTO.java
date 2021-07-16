package com.devaneios.turmadeelite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassStatusNameDTO {
    @NotBlank
    private String name;

    @NotNull
    private Boolean isActive;
}
