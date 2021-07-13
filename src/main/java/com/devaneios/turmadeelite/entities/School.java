package com.devaneios.turmadeelite.entities;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "school")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String identifier;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "school_id")
    private List<Manager> managers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "teacher_id")
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<Teacher> students = new ArrayList<>();

    public void addManager(Manager manager){
        this.managers.add(manager);
        manager.setSchool(this);
    }

    public void removeManager(Manager manager){
        this.managers.remove(manager);
        manager.setSchool(null);
    }

    public void addTeacher(Teacher teacher){
        this.teachers.add(teacher);
        teacher.setSchool(this);
    }

    public void removeTeacher(Teacher teacher){
        this.teachers.remove(teacher);
        teacher.setSchool(null);
    }
}
