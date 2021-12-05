package com.devaneios.turmadeelite.external.courses;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;

import java.io.IOException;
import java.util.List;

public interface ExternalCoursesService {
    List<SchoolClassViewDTO> getAllCourses(String authUuid) throws IOException, ExternalServiceAuthenticationException;
    List<SchoolClassViewDTO> getCoursesFromTeacher(String authUuid) throws IOException;
}
