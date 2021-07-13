package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentCreateDTO {
    @NotBlank
    @Email
    String email;

    @Size(min = 3)
    private String name;

    @NotBlank
    private String registry;

    @NotNull
    private Boolean isActive;

    private String language;

    public UserCredentials toCredentials(){
        return UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .isActive(isActive)
                .role(Role.STUDENT)
                .build();
    }
}
