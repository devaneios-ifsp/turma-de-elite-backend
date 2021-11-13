package com.devaneios.turmadeelite.external.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/external/activities")
@AllArgsConstructor
public class ExternalActivitiesController {

    private final ExternalActivitiesService externalActivitiesService;

    @IsTeacher
    @GetMapping
    public List<ActivityViewDTO> getAllActivities(Authentication autenthication) throws IOException {
        String authUuid = (String) autenthication.getPrincipal();
        return this.externalActivitiesService.getAllActivities(authUuid);
    }

}
