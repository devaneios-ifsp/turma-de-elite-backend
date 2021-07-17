package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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

    @Query("SELECT t FROM Teacher t JOIN t.credentials c WHERE c.name LIKE :name%")
    Optional<List<Teacher>> findByNameContainingIgnoreCase(String name);
}
