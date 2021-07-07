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
    @JoinColumn(name = "activity_delivery_id")
    private List<ActivityDelivery> deliveries = new ArrayList<>();
}
