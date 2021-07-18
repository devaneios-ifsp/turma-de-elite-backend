package com.devaneios.turmadeelite.security.verifiers;

import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidityVerifierFactory {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final TeacherRepository teacherRepository;

    public ValidityVerifier fromAuthenticationInfo(AuthenticationInfo authenticationInfo) {
        switch (authenticationInfo.getRole()){
            case ADMIN:
                return new AdminVerifier(this.userRepository,authenticationInfo);
            case MANAGER:
                return new ManagerVerifier(this.managerRepository,authenticationInfo);
            case TEACHER:
                return new TeacherVerifier(this.teacherRepository,authenticationInfo);
            default:
                throw new IllegalArgumentException();
        }
    }
}
