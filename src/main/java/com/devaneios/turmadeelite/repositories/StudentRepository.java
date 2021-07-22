package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student,Long> {

    @Modifying
    @Query(value = "INSERT INTO student (student_id,school_id,registry) VALUES (:userId,:schoolId,:registry);",nativeQuery = true)
    void insertUserAsStudent(Long userId, Long schoolId,String registry);

    @Query(value = "SELECT s FROM Student s JOIN s.school sc JOIN s.credentials c WHERE sc.id=:schoolId AND c.role='STUDENT'")
    Page<Student> findAllBySchoolId(Long schoolId, Pageable pageable);

    Optional<Student> findByRegistryAndSchoolId(String registry,Long schoolId);

    @Query(value = "SELECT s FROM Student s JOIN s.school sc  WHERE sc.id=:schoolId AND s.id=:id")
    Optional<Student> findByIdAndSchoolId(Long schoolId, Long id);

    @Query(value = "SELECT s FROM Student s JOIN s.school sc JOIN s.credentials c WHERE sc.id=:schoolId AND c.role='STUDENT' AND s.registry LIKE :registry")
    List<Student> findStudentsByRegistrySimilarity(String registry, Long schoolId);

    @Query(value = "SELECT s FROM Student s JOIN FETCH s.school sc JOIN s.credentials c WHERE c.authUuid=:authUuid")
    Optional<Student> findByAuthUuidWithSchool(String authUuid);

    @Query(value = "SELECT s FROM Student s JOIN s.credentials c WHERE c.authUuid=:studentAuthUuid")
    Optional<Student> findByAuthUuid(String studentAuthUuid);

    @Query("SELECT st FROM Student st JOIN st.classMembership cm JOIN cm.schoolClass c WHERE c.id=:classId")
    List<Student> findAllByClassId(Long classId);
}
