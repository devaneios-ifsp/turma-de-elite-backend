package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Student {
    @Id
    @Column(name = "student_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private UserCredentials credentials;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<StudentClassMembership> classMembership = new ArrayList<>();

    @ManyToOne
    public School school;

    private String registry;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true,mappedBy = "student")
    private List<ActivityDelivery> deliveries = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "student_achievements",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    List<Achievement> studentAchievements = new ArrayList<>();
}
