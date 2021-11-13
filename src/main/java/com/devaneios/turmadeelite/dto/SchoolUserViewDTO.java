package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.Teacher;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SchoolUserViewDTO {

    public String id;
    public String externalId;
    public String email;
    public String name;
    public SchoolViewDTO school;
    public Boolean isActive;

    @Builder.Default
    private Boolean isFromLms = false;

    public SchoolUserViewDTO(Manager manager){
        this.id = manager.getId().toString();
        this.email = manager.getCredentials().getEmail();
        this.name = manager.getCredentials().getName();
        this.school = manager.getSchool() == null ? null : new SchoolViewDTO(manager.getSchool());
        this.isActive = manager.getCredentials().getIsActive();
    }

    public SchoolUserViewDTO(Teacher teacher){
        this.id = teacher.getId().toString();
        this.email = teacher.getCredentials().getEmail();
        this.name = teacher.getCredentials().getName();
        this.school = teacher.getSchool() == null ? null : new SchoolViewDTO(teacher.getSchool());
        this.isActive = teacher.getCredentials().getIsActive();
        this.isFromLms = false;
    }
}
