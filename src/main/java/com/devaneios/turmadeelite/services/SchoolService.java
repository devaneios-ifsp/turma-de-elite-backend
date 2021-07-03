package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.School;
import org.springframework.data.domain.Page;

public interface SchoolService {
    void createSchool(School school);
    Page<School> getPaginatedSchools(int size, int pageNumber);
}
