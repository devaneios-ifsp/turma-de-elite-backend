package com.devaneios.turmadeelite.security.verifiers;

import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.Student;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.UnexpectedAuthenticationException;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;

import javax.naming.AuthenticationException;

public class StudentVerifier implements ValidityVerifier {
    private final StudentRepository studentRepository;
    private final AuthenticationInfo authenticationInfo;

    public StudentVerifier(StudentRepository studentRepository, AuthenticationInfo authenticationInfo) {
        this.studentRepository = studentRepository;
        this.authenticationInfo = authenticationInfo;
    }

    @Override
    public void verify() throws AuthenticationException {
        Student student = this.studentRepository
                .findByAuthUuidWithSchool(this.authenticationInfo.getPrincipal())
                .orElseThrow(UnexpectedAuthenticationException::new);
        School school = student.getSchool();
        UserCredentials credentials = student.getCredentials();
        if(!school.getIsActive() || !credentials.getIsActive()){
            throw new UnexpectedAuthenticationException();
        }
    }
}
