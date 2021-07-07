package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_delivery")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ActivityDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime deliveryTimestamp;

    private Float gradeReceived;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Activity activity;
}
