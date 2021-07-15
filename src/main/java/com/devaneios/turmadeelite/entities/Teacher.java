package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teacher")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private Long id;

//    @Column(unique = true)
//    @Size(min = 11, max = 11, message = "O CPF deve possuir 11 dígitos")
//    private String cpf;
//
//    @Column(unique = true)
//    @Size(min = 11, max = 11, message = "O número telefone deve possuir 11 dígitos.")
//    private String phone;

    @OneToOne
    @MapsId
    @JoinColumn(name = "teacher_id")
    private UserCredentials credentials;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "teacher_id")
    private List<StudentClassMembership> classMembership = new ArrayList<>();

    @ManyToOne
    public School school;

}