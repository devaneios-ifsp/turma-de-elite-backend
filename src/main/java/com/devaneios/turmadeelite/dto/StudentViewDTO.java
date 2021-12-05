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
    private String externalId;
    public String email;
    public String name;
    public String registry;
    public Boolean isActive;

    @Builder.Default
    private Boolean isFromLms = false;

    public StudentViewDTO(Student student){
        this.id = student.getId();
        this.email = student.getCredentials().getEmail();
        this.name = student.getCredentials().getName();
        this.registry = student.getRegistry();
        this.isActive = student.getCredentials().getIsActive();
        this.isFromLms = false;
    }

    public static StudentViewDTO fromClassroom(com.google.api.services.classroom.model.Student student){
        return StudentViewDTO.builder()
                .id(null)
                .externalId(student.getUserId())
                .email(student.getProfile().getEmailAddress())
                .name(student.getProfile().getName().getFullName())
                .registry(student.getUserId())
                .isActive(true)
                .isFromLms(true)
                .build();
    }
}
