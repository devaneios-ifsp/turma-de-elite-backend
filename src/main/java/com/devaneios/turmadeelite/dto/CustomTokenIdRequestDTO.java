package com.devaneios.turmadeelite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomTokenIdRequestDTO {
        String email;
        String password;
        boolean returnSecureToken = true;
}
