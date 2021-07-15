package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "teacher_class_membership")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TeacherClassMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    private Boolean isActive;
}
