package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    @Query("SELECT t FROM Teacher t JOIN t.credentials c JOIN t.school s WHERE t.id=:id")
    Optional<Teacher> findTeacherByIdWithSchoolAndCredentials(Long id);
}
