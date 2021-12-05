package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "external_class_config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExternalClassConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalClassId;
    private Boolean isClosed;
    private Float goldPercent;
    private Float silverPercent;
    private Float bronzePercent;
}
