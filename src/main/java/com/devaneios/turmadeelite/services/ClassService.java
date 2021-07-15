package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.ClassCreateDTO;
import com.devaneios.turmadeelite.dto.ClassStatusNameDTO;
import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.SchoolViewDTO;
import com.devaneios.turmadeelite.entities.SchoolClass;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassService {
    void createClass(ClassCreateDTO classCreateDTO,String managerAuthUuid);

    SchoolClass getSchoolClassById(Long id, String managerAuthUUid);

    Page<SchoolClass> getAllClasses(String managerAuthUuid,int pageNumber,int size);

    void updateStatusAndName(Long id, String managerAuthUuid, ClassStatusNameDTO statusNameDTO);

    void updateStudentStatus(Long classId, Long studentId, Boolean status, String managerAuthUuid);

    void updateTeacherStatus(Long classId, Long teacherId, Boolean status, String managerAuthUuid);
}
