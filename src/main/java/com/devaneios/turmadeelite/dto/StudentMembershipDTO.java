package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.StudentClassMembership;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentMembershipDTO {
    private StudentViewDTO student;
    private Boolean isActive;

    public StudentMembershipDTO(StudentClassMembership studentClassMembership){
        this.student = new StudentViewDTO(studentClassMembership.getStudent());
        this.isActive = studentClassMembership.getIsActive();
    }
}
