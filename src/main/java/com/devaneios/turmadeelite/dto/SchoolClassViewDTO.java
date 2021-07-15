package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SchoolClassViewDTO {
    private Long id;
    private String name;
    private List<StudentMembershipDTO> students;
    private List<TeacherMembershipDTO> teachers;
    private Boolean isActive;
    private Boolean isDone;

    public SchoolClassViewDTO(SchoolClass schoolClass){
        this.id = schoolClass.getId();
        this.name = schoolClass.getName();

        this.students = schoolClass
                .getStudentsMemberships()
                .stream()
                .map(StudentMembershipDTO::new)
                .collect(Collectors.toList());

        this.teachers = schoolClass
                .getTeachersMemberships()
                .stream()
                .map(TeacherMembershipDTO::new)
                .collect(Collectors.toList());

        this.isActive = schoolClass.getIsActive();
        this.isDone = schoolClass.getIsDone();
    }
}
