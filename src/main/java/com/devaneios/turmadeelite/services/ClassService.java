package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.ClassCreateDTO;
import com.devaneios.turmadeelite.dto.ClassStatusNameDTO;
import com.devaneios.turmadeelite.entities.SchoolClass;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassService {
    void createClass(ClassCreateDTO classCreateDTO,String managerAuthUuid);

    SchoolClass getSchoolClassByIdAsManager(Long id, String managerAuthUUid);

    SchoolClass getSchoolClassByIdAsTeacher(Long id, String teacherAuthUuid);

    Page<SchoolClass> getAllClasses(String managerAuthUuid,int pageNumber,int size);

    void updateStatusAndName(Long id, String managerAuthUuid, ClassStatusNameDTO statusNameDTO);

    void updateStudentStatus(Long classId, Long studentId, Boolean status, String managerAuthUuid);

    void updateTeacherStatus(Long classId, Long teacherId, Boolean status, String managerAuthUuid);

    List<SchoolClass> getAllClassesOfTeacher(String teacherAuthUuid);

    Page<SchoolClass> getAllClassesOfTeacher(String teacherAuthUuid,Integer pageNumber, Integer pageSize);

    void addTeacherToClass(String managerAuthUuid, Long classId, Long teacherId);

    void addStudentToClass(String managerAuthUuid, Long classId, Long studentId);

    void closeClass(Long id, String teacherAuthUuid);
}
