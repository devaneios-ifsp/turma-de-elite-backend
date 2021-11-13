package com.devaneios.turmadeelite.external.students;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import com.devaneios.turmadeelite.security.guards.IsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/external/students")
@AllArgsConstructor
public class ExternalStudentsController {

    private final ExternalStudentsService externalStudentsService;

    @IsManager
    @GetMapping
    public List<SchoolUserViewDTO> getAllStudents(Authentication autenthication) throws IOException {
        String authUuid = (String) autenthication.getPrincipal();
        return this.externalStudentsService.getAllStudents(authUuid);
    }

}
