package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.UserCredentials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminViewDTO {
    public Long id;
    public String email;
    public String name;

    public AdminViewDTO(UserCredentials userCredentials){
        this.id = userCredentials.getId();
        this.email = userCredentials.getEmail();
        this.name = userCredentials.getName();
    }
}
