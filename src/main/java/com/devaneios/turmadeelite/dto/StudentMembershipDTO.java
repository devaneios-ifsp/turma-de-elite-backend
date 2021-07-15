package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.StudentClassMembership;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentMembershipDTO {
    private StudentViewDTO student;
    private Boolean isActive;

    public StudentMembershipDTO(StudentClassMembership studentClassMembership){
        this.student = new StudentViewDTO(studentClassMembership.getStudent());
        this.isActive = studentClassMembership.getIsActive();
    }
}
