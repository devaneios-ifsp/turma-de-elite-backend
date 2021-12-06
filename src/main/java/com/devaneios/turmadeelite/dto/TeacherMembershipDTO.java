package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.TeacherClassMembership;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TeacherMembershipDTO {
    private SchoolUserViewDTO teacher;
    private Boolean isActive;

    public TeacherMembershipDTO(TeacherClassMembership teacherMembership){
        this.teacher = new SchoolUserViewDTO(teacherMembership.getTeacher());
        this.isActive = teacherMembership.getIsActive();
    }
}
