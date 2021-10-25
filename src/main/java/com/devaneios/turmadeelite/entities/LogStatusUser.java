package com.devaneios.turmadeelite.entities;

import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "log_status_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LogStatusUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserCredentials userCredentials;

    private DateTime date_action;

    @Column(name = "old_is_active")
    private Boolean old_is_active;
}
