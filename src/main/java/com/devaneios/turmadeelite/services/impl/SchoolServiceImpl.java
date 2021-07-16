package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.exceptions.AlreadyRegisteredSchool;
import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.services.SchoolService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository repository;
    private final ManagerRepository managerRepository;

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

    @Override
    public School getSchoolById(Long schoolId) {
        return this.repository.findById(schoolId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void updateSchoolById(Long schoolId, SchoolCreateDTO schoolCreateDTO) {
        Optional<School> byIdentifier = this.repository.findByIdentifier(schoolCreateDTO.getIdentifier());
        byIdentifier.ifPresent(userCredentials -> {
            if(userCredentials.getId() != schoolId){
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        });
        School school = this.repository.findById(schoolId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        school.setIdentifier(schoolCreateDTO.getIdentifier());
        school.setName(schoolCreateDTO.getName());
        school.setIsActive(schoolCreateDTO.getIsActive());
        this.repository.save(school);
    }

    @Override
    public List<School> getSchoolsByNameSimilarity(String name) {
        return this.repository.findByNameContainingIgnoreCase(name);
    }

    public School findSchoolByManagerAuthUuid(String authUuid){
        return this.managerRepository
                .findManagerByAuthUuidWithSchoolAndCredentials(authUuid)
                .map(Manager::getSchool)
                .filter(Objects::nonNull)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
