package com.devaneios.turmadeelite.external.teachers;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;

import java.io.IOException;
import java.util.List;

public interface ExternalTeachersService {
    List<SchoolUserViewDTO> getAllTeachers(String authUuid) throws IOException;
}
