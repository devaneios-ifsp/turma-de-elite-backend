package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private LocalDateTime maxDeliveryDate;

    @OneToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Builder.Default
    @ManyToMany(mappedBy = "classActivities")
    List<SchoolClass> classes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "activity")
    @Builder.Default
    private List<ActivityDelivery> deliveries = new ArrayList<>();
}
