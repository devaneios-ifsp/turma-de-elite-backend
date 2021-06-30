package com.devaneios.turmadeelite.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminCreateDTO {
    @NotBlank
    @Email
    String email;

    @Size(min = 3)
    private String name;

    private String language;
}