package com.devaneios.turmadeelite.external.teachers;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.google.api.services.classroom.model.Teacher;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ExternalTeachersService {
    List<SchoolUserViewDTO> getAllTeachers(String authUuid) throws IOException;

    SchoolUserViewDTO getTeacherByExternalId(String externalId, String authUuid) throws IOException;
}
