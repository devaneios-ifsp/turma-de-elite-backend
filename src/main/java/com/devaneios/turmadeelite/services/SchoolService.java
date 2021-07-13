package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.entities.School;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SchoolService {
    void createSchool(School school);
    Page<School> getPaginatedSchools(int size, int pageNumber);
    School getSchoolById(Long schoolId);
    void updateSchoolById(Long schoolId, SchoolCreateDTO schoolCreateDTO);
    List<School> getSchoolsByNameSimilarity(String name);
    School findSchoolByManagerAuthUuid(String managerAuthUuid);
}
