package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.entities.StudentClassMembership;
import com.devaneios.turmadeelite.entities.TeacherClassMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolClassRepository extends CrudRepository<SchoolClass,Long> {
    @Modifying
    @Query(value = "INSERT INTO student_class_membership (student_id,class_id) VALUES (:studentId,:classId) ;",nativeQuery = true)
    void addStudentToClass(Long studentId, Long classId);

    @Modifying
    @Query(value = "INSERT INTO teacher_class_membership (teacher_id,class_id) VALUES (:teacherId,:classId) ;",nativeQuery = true)
    void addTeacherToClass(Long teacherId, Long classId);

    @Query("SELECT s FROM SchoolClass s " +
            "JOIN s.school sc " +
            "WHERE s.id=:classId AND sc.id=:schoolId")
    SchoolClass findSchoolClassByClassIdAndSchoolId(Long classId, Long schoolId);

    @Query("SELECT s FROM StudentClassMembership s " +
            "JOIN s.schoolClass sc " +
            "JOIN FETCH s.student st " +
            "WHERE sc.id=:classId")
    List<StudentClassMembership> findStudentsMembershipsBySchoolId(Long classId);

    @Query("SELECT t FROM TeacherClassMembership t " +
            "JOIN t.schoolClass sc " +
            "JOIN FETCH t.teacher tc " +
            "WHERE sc.id=:classId")
    List<TeacherClassMembership> findTeachersMembershipsBySchoolId(Long classId);

    @Query("SELECT s FROM SchoolClass s " +
            "JOIN s.school sc " +
            "WHERE sc.id=:schoolId")
    Page<SchoolClass> findAllSchoolClassesBySchoolId(Long schoolId, Pageable pageRequest);

    @Modifying
    @Query(value = "UPDATE class SET name=:newName,is_active=:newStatus WHERE id=:classId ;",nativeQuery = true)
    void updateStatusAndNameByClassIdAndSchoolId(Long classId,String newName,Boolean newStatus);

    @Modifying
    @Query(value = "UPDATE student_class_membership SET is_active=:status WHERE class_id=:classId AND student_id=:studentId ;",nativeQuery = true)
    void updateStudentStatusInClass(Long classId, Long studentId, Boolean status);

    @Modifying
    @Query(value = "UPDATE teacher_class_membership SET is_active=:status WHERE class_id=:classId AND teacher_id=:teacherId ;",nativeQuery = true)
    void updateTeacherStatusInClass(Long classId, Long teacherId, Boolean status);

    @Query("SELECT s FROM SchoolClass s JOIN s.teachersMemberships tm JOIN tm.teacher t WHERE t.id=:teacherId")
    List<SchoolClass> findAllSchoolClassesByTeacher(Long teacherId);

    @Query("SELECT s FROM SchoolClass s JOIN s.classActivities c WHERE c.id=:activityId")
    List<SchoolClass> findAllSchoolClassesByActivityId(Long activityId);

    @Query("SELECT s FROM SchoolClass s JOIN s.studentsMemberships sm JOIN sm.student st WHERE st.id=:id")
    List<SchoolClass> findAllByStudentId(Long id);
}
