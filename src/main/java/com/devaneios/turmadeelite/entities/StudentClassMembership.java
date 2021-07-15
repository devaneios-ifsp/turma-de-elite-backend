package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "student_class_membership")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StudentClassMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    private Boolean isActive;
}
