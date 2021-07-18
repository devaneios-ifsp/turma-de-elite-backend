package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "activity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double punctuation;

    private Boolean isVisible;

    private Boolean isActive;

    private Boolean isDeliverable;

    private LocalDateTime maxDeliveryDate;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Builder.Default
    @ManyToMany(mappedBy = "classActivities")
    List<SchoolClass> classes = new ArrayList<>();
//
//    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "activity")
//    private List<ActivityDelivery> deliveries = new ArrayList<>();
}
