package com.devaneios.turmadeelite.external.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.dto.StudentActivitiesDTO;
import com.devaneios.turmadeelite.entities.Activity;

import java.io.IOException;
import java.util.List;

public interface ExternalActivitiesService {
    List<ActivityViewDTO> getActivitiesFromTeacher(String authUuid) throws IOException;
    List<StudentActivitiesDTO> getActivitiesFromStudent(String authUuid) throws IOException;
    ActivityViewDTO getExternalActivityById(String externalId, String authUuid) throws IOException;
}
