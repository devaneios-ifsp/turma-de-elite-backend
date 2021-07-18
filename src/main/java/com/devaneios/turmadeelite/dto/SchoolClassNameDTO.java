package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SchoolClassNameDTO {
    public Long id;
    public String name;

    public SchoolClassNameDTO(SchoolClass schoolClass){
        this.id = schoolClass.getId();
        this.name = schoolClass.getName();
    }
}
