package com.devaneios.turmadeelite.external.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.dto.StudentActivitiesDTO;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/external/activities")
@AllArgsConstructor
public class ExternalActivitiesController {

    private final ExternalActivitiesService externalActivitiesService;

    @IsTeacher
    @GetMapping("/authenticated-teacher")
    public List<ActivityViewDTO> getActivitiesFromTeacher(Authentication autenthication) throws IOException {
        String authUuid = (String) autenthication.getPrincipal();
        return this.externalActivitiesService.getActivitiesFromTeacher(authUuid);
    }

    @IsStudent
    @GetMapping("/authenticated-student")
    public List<StudentActivitiesDTO> getActivitiesFromStudent(Authentication autenthication) throws IOException {
        String authUuid = (String) autenthication.getPrincipal();
        return this.externalActivitiesService.getActivitiesFromStudent(authUuid);
    }

    @IsTeacher
    @GetMapping("/{externalId}")
    public ActivityViewDTO getTeacherExternalActivityById(@PathVariable String externalId, Authentication authentication) throws IOException {
        return this.externalActivitiesService.getExternalActivityById(externalId, (String) authentication.getPrincipal());
    }

}
