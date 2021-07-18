package com.devaneios.turmadeelite.security.verifiers;

import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.UnexpectedAuthenticationException;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import org.springframework.security.core.AuthenticationException;


public class TeacherVerifier implements ValidityVerifier {

    private final TeacherRepository teacherRepository;
    private final AuthenticationInfo authenticationInfo;
    public TeacherVerifier(TeacherRepository teacherRepository, AuthenticationInfo authenticationInfo) {
        this.teacherRepository = teacherRepository;
        this.authenticationInfo = authenticationInfo;
    }

    @Override
    public void verify() throws AuthenticationException {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid((String) authenticationInfo.getPrincipal())
                .orElseThrow(() -> new UnexpectedAuthenticationException());
        School school = teacher.getSchool();
        UserCredentials credentials = teacher.getCredentials();
        if(!school.getIsActive() || !credentials.getIsActive()){
            throw new UnexpectedAuthenticationException();
        }
    }
}
