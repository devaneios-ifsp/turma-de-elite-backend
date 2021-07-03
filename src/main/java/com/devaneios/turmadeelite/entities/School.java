package com.devaneios.turmadeelite.entities;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import lombok.*;

import javax.persistence.*;

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

}
