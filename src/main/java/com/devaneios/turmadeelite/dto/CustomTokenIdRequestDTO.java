package com.devaneios.turmadeelite.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomTokenIdRequestDTO {
        String email;
        String password;
        boolean returnSecureToken = true;
}
