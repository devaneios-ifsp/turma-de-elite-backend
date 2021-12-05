package com.devaneios.turmadeelite.external.teachers;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.devaneios.turmadeelite.security.guards.IsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/external/teachers")
@AllArgsConstructor
public class ExternalTeachersController {

    private final ExternalTeachersService externalTeachersService;

    @IsManager
    @GetMapping
    public List<SchoolUserViewDTO> getAllTeachers(Authentication autenthication) throws IOException {
        String authUuid = (String) autenthication.getPrincipal();
        return this.externalTeachersService.getAllTeachers(authUuid);
    }

    @IsManager
    @GetMapping("/{externalId}")
    public ResponseEntity<SchoolUserViewDTO> getTeacherByExternalId(@PathVariable String externalId, Authentication autenthication) throws IOException {
        SchoolUserViewDTO externalTeacher = this.externalTeachersService.getTeacherByExternalId(externalId, (String) autenthication.getPrincipal());
        return ResponseEntity.ok(externalTeacher);
    }

}
