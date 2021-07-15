package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.TeacherClassMembership;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherMembershipDTO {
    private SchoolUserViewDTO teacher;
    private Boolean isActive;

    public TeacherMembershipDTO(TeacherClassMembership teacherMembership){
        this.teacher = new SchoolUserViewDTO(teacherMembership.getTeacher());
        this.isActive = teacherMembership.getIsActive();
    }
}
