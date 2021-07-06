package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.UserCredentials;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ManagerViewDTO {
    public Long id;
    public String email;
    public String name;
    public SchoolViewDTO school;
    public Boolean isActive;

    public ManagerViewDTO(Manager manager){
        this.id = manager.getId();
        this.email = manager.getCredentials().getEmail();
        this.name = manager.getCredentials().getName();
        this.school = manager.getSchool() == null ? null : new SchoolViewDTO(manager.getSchool());
        this.isActive = manager.getCredentials().getIsActive();
    }
}
