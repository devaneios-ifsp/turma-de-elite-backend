package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.School;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SchoolViewDTO {
    public Long id;
    public String name;
    public String identifier;
    public Boolean isActive;

    public SchoolViewDTO(School school){
        this.id = school.getId();
        this.name = school.getName();
        this.identifier = school.getIdentifier();
        this.isActive = school.getIsActive();
    }
}
