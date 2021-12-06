package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "external_student_grade")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExternalStudentGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Double grade;

    private String externalClassId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private UserCredentials student;
}
