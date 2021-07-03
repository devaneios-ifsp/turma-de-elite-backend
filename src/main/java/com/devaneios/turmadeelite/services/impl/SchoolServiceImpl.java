package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.exceptions.AlreadyRegisteredSchool;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.services.SchoolService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository repository;

    @Override
    @Transactional
    public void createSchool(School school) {
        boolean exists = repository.existsByIdentifier(school.getIdentifier());
        if(exists){
            throw new AlreadyRegisteredSchool(school.getIdentifier());
        }
        repository.save(school);
    }

    @Override
    public Page<School> getPaginatedSchools(int size, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<School> schoolPage = this.repository.findAll(pageable);
        return schoolPage;
    }
}
