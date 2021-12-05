package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teacher")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "teacher_id")
    private UserCredentials credentials;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "teacher_id")
    private List<TeacherClassMembership> classMembership = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "teacher_id")
    private List<Activity> activities;

    @ManyToOne
    @JoinColumn(name = "school_id")
    public School school;

}