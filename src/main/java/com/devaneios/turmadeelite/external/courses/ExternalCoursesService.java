package com.devaneios.turmadeelite.external.courses;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;

import java.io.IOException;
import java.util.List;

public interface ExternalCoursesService {
    public List<SchoolClassViewDTO> getAllConvertedCourses(String authUuid) throws IOException, ExternalServiceAuthenticationException;
}
