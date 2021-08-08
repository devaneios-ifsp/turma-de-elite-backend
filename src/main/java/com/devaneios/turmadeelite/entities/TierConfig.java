package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tier_config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TierConfig {

    @Id
    @Column(name = "class_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    private Float goldPercent;
    private Float silverPercent;
    private Float bronzePercent;
}
