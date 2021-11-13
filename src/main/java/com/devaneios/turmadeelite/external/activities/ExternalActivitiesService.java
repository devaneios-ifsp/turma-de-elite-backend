package com.devaneios.turmadeelite.external.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;

import java.io.IOException;
import java.util.List;

public interface ExternalActivitiesService {
    List<ActivityViewDTO> getAllActivities(String authUuid) throws IOException;
}
