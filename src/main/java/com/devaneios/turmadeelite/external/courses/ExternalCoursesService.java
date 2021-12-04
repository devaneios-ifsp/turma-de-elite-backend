package com.devaneios.turmadeelite.external.courses;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

public interface ExternalCoursesService {
    List<SchoolClassViewDTO> getAllCourses(String authUuid) throws IOException, ExternalServiceAuthenticationException;
    List<SchoolClassViewDTO> getCoursesFromTeacher(String authUuid) throws IOException;
    //SchoolClassViewDTO getExternalClassById(String externalId, String authUuid) throws IOException;
}
