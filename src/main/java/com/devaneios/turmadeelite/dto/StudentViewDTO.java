package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Student;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentViewDTO {
    public Long id;
    public String email;
    public String name;
    public String registry;
    public Boolean isActive;

    public StudentViewDTO(Student student){
        this.id = student.getId();
        this.email = student.getCredentials().getEmail();
        this.name = student.getCredentials().getName();
        this.registry = student.getRegistry();
        this.isActive = student.getCredentials().getIsActive();
    }
}
