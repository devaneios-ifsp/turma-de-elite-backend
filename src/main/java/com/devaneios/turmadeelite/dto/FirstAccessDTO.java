package com.devaneios.turmadeelite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FirstAccessDTO {
    String email;
    String password;
    String firstAccessToken;
}
