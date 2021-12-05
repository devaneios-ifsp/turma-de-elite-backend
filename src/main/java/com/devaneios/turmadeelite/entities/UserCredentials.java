package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_credentials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "auth_uuid")
    private String authUuid;

    @Column(name = "first_access_token")
    private String firstAccessToken;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "accession_date")
    private Date accessionDate;

    @OneToOne(mappedBy = "credentials",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Manager manager;

    @OneToOne(mappedBy = "credentials",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Teacher teacher;

}
