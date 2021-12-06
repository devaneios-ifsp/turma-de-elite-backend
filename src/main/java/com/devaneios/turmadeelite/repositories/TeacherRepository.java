package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.entities.TeacherClassMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    @Query("SELECT t FROM Teacher t JOIN t.credentials c JOIN t.school s WHERE t.id=:teacherId AND s.id=:schoolId")
    Optional<Teacher> findTeacherByIdWithSchoolAndCredentials(Long teacherId,Long schoolId);

    @Modifying
    @Query(value = "INSERT INTO teacher (teacher_id,school_id) VALUES (:teacherId,:schoolId);",nativeQuery = true)
    void insertUserAsTeacher(Long teacherId, Long schoolId);

    @Query("SELECT t FROM Teacher t JOIN t.school s WHERE s.id=:id")
    Page<Teacher> findAllBySchoolId(Long id, Pageable pageable);

    @Query("SELECT t FROM Teacher t JOIN t.credentials c JOIN t.school s WHERE c.email LIKE :email AND s.id=:schoolId")
    List<Teacher> findTeacherByEmailLikeAndSchoolId(String email, Long schoolId);

    @Query("SELECT t FROM Teacher t JOIN t.credentials c JOIN t.school s WHERE c.authUuid=:authUuid")
    Optional<Teacher> findByAuthUuid(String authUuid);

    @Query("SELECT t FROM Teacher t JOIN t.credentials c WHERE c.name LIKE :name%")
    Optional<List<Teacher>> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM Teacher t WHERE t.school_id = :schoolId", nativeQuery = true)
    List<Teacher> findBySchool(Long schoolId);

    @Query(value = "SELECT class_id FROM teacher_class_membership u WHERE teacher_id = :teacher_id", nativeQuery = true)
    List<Long> getClassByTeacher(Long teacher_id);
}
