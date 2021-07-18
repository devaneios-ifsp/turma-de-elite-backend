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

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "school_id")
    private List<Manager> managers = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "school_id")
    private List<Teacher> teachers = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "school_id")
    private List<Teacher> students = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "school_id")
    private List<SchoolClass> classes = new ArrayList<>();

    public void addManager(Manager manager){
        this.managers.add(manager);
        manager.setSchool(this);
    }

    public void removeManager(Manager manager){
        this.managers.remove(manager);
        manager.setSchool(null);
    }
}
