package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.ClassCreateDTO;
import com.devaneios.turmadeelite.dto.ClassStatusNameDTO;
import com.devaneios.turmadeelite.dto.SchoolClassNameDTO;
import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.repositories.SchoolClassRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.services.ClassService;
import com.devaneios.turmadeelite.services.SchoolService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final SchoolClassRepository classRepository;
    private final SchoolService schoolService;
    private final TeacherRepository teacherRepository;

    @Transactional
    @Override
    public void createClass(ClassCreateDTO classCreateDTO, String managerAuthUuid) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        SchoolClass schoolClass = SchoolClass
                .builder()
                .name(classCreateDTO.getClassName())
                .isActive(classCreateDTO.getIsActive())
                .isDone(false)
                .school(school)
                .build();

        SchoolClass savedSchoolClass = this.classRepository.save(schoolClass);
        List<Long> studentsId = classCreateDTO.getStudentsId();
        List<Long> teachersId = classCreateDTO.getTeachersId();
        studentsId.forEach(student -> {
            this.classRepository.addStudentToClass(student, savedSchoolClass.getId());
        });
        teachersId.forEach( teacher -> {
            this.classRepository.addTeacherToClass(teacher, savedSchoolClass.getId());
        });
    }

    @Override
    public SchoolClass getSchoolClassById(Long id, String managerAuthUuid) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        SchoolClass schoolClass = this.classRepository.findSchoolClassByClassIdAndSchoolId(id, school.getId());
        List<StudentClassMembership> students = this.classRepository.findStudentsMembershipsBySchoolId(id);
        List<TeacherClassMembership> teachers = this.classRepository.findTeachersMembershipsBySchoolId(id);
        schoolClass.setStudentsMemberships(students);
        schoolClass.setTeachersMemberships(teachers);
        return schoolClass;
    }

    @Override
    public Page<SchoolClass> getAllClasses(String managerAuthUuid,int pageNumber,int size) {
        Pageable pageRequest = PageRequest.of(pageNumber,size);
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        return this.classRepository
                .findAllSchoolClassesBySchoolId(school.getId(), pageRequest)
                .map( schoolClass -> {
                    List<StudentClassMembership> students = this.classRepository.findStudentsMembershipsBySchoolId(schoolClass.getId());
                    List<TeacherClassMembership> teachers = this.classRepository.findTeachersMembershipsBySchoolId(schoolClass.getId());
                    schoolClass.setStudentsMemberships(students);
                    schoolClass.setTeachersMemberships(teachers);
                    return schoolClass;
                });
    }

    @Override
    public List<SchoolClass> getAllClassesOfTeacher(String teacherAuthUuid){
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.classRepository.findAllSchoolClassesByTeacher(teacher.getId());
    }

    @Transactional
    @Override
    public void addTeacherToClass(String managerAuthUuid, Long classId, Long teacherId) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        if(school == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        this.classRepository.addTeacherToClass(teacherId, classId);
    }

    @Override
    @Transactional
    public void updateStatusAndName(Long id, String managerAuthUuid, ClassStatusNameDTO statusNameDTO) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        SchoolClass schoolClass = this.classRepository.findSchoolClassByClassIdAndSchoolId(id, school.getId());
        if(schoolClass == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        this.classRepository.updateStatusAndNameByClassIdAndSchoolId(id,statusNameDTO.getName(),statusNameDTO.getIsActive());
    }

    @Override
    @Transactional
    public void updateStudentStatus(Long classId, Long studentId, Boolean status, String managerAuthUuid) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        SchoolClass schoolClass = this.classRepository.findSchoolClassByClassIdAndSchoolId(classId, school.getId());
        this.classRepository.updateStudentStatusInClass(classId,studentId,status);
    }

    @Override
    @Transactional
    public void updateTeacherStatus(Long classId, Long teacherId, Boolean status, String managerAuthUuid) {
        School school = schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        SchoolClass schoolClass = this.classRepository.findSchoolClassByClassIdAndSchoolId(classId, school.getId());
        this.classRepository.updateTeacherStatusInClass(classId,teacherId,status);
    }
}
