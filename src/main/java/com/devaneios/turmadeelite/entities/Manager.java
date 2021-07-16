package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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
    @JoinColumn(name ="school_id")
    private School school;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Objects.equals(id, manager.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
