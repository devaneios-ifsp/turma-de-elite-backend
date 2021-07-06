package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.UserCredentials;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminViewDTO {
    public Long id;
    public String email;
    public String name;
    public Boolean isActive;

    public AdminViewDTO(UserCredentials userCredentials){
        this.id = userCredentials.getId();
        this.email = userCredentials.getEmail();
        this.name = userCredentials.getName();
        this.isActive = userCredentials.getIsActive();
    }
}
