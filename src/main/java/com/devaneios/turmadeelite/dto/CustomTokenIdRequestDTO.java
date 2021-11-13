package com.devaneios.turmadeelite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomTokenIdRequestDTO {
        String email;
        String password;
        boolean returnSecureToken = true;
}
