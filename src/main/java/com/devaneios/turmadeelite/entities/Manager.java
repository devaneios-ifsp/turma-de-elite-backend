package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "manager")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Manager {

    @Id
    @Column(name = "manager_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "manager_id")
    private UserCredentials credentials;

    @ManyToOne
    private School school;
}
