package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "class_id")
    private List<StudentClassMembership> studentsMemberships = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "class_id")
    private List<TeacherClassMembership> teachersMemberships = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "school_id")
    public School school;

    @ManyToMany
    @JoinTable(
            name = "class_activities",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    List<Activity> classActivities;

    private Boolean isActive;

    private Boolean isDone;
}
