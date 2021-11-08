package com.devaneios.turmadeelite.external.students;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;

import java.io.IOException;
import java.util.List;

public interface ExternalStudentsService {
    List<SchoolUserViewDTO> getAllStudents(String authUuid) throws IOException;
}
