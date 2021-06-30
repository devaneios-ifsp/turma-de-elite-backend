package com.devaneios.turmadeelite.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FirstAccessDTO {
    @NotBlank
    @Email(message = "E-mail inválido")
    String email;
    @NotBlank
    @Length(min = 6,message = "Senha deve ter no mínimo 6 caracteres")
    String password;

    @NotBlank(message = "Token enviado por E-mail é obrigatório")
    String firstAccessToken;
}
