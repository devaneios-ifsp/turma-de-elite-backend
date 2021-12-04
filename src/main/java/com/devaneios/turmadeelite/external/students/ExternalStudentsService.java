package com.devaneios.turmadeelite.external.students;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.dto.StudentViewDTO;

import java.io.IOException;
import java.util.List;

public interface ExternalStudentsService {
    List<SchoolUserViewDTO> getAllStudents(String authUuid) throws IOException;

    StudentViewDTO getStudentByExternalId(String externalId, String authUuid) throws IOException;
}
