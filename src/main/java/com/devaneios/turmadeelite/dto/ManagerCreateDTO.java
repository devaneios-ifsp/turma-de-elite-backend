package com.devaneios.turmadeelite.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ManagerCreateDTO {
    @NotBlank
    @Email
    String email;

    @Size(min = 3)
    private String name;

    private Long schoolId;

    @NotNull
    private Boolean isActive;

    private String language;
}
