package com.devaneios.turmadeelite.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "admin_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "firebase_uuid")
    private String firebaseUuid;

    @Column(name = "first_access_token")
    private String firstAccessToken;

    @Column(name = "name",nullable = false)
    private String name;

}
