package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.UserCredentials;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserCredentials,Long> {
    public Optional<UserCredentials> findUserByEmailAndFirstAccessToken(String email, String firstAccessToken);

    Optional<UserCredentials> findByFirstAccessToken(String firstAccessToken);

    Optional<UserCredentials> findByAuthUuid(String authUuid);

    boolean existsByEmail(String email);

    Optional<UserCredentials> findByEmail(String email);

    @Query("FROM UserCredentials u WHERE u.role='ADMIN'")
    Page<UserCredentials> findAllAdmins(Pageable pageable);

    @Query("SELECT u FROM UserCredentials u WHERE u.name LIKE :name% AND u.role='ADMIN'")
    List<UserCredentials> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT count(u.id) FROM user_credentials AS u WHERE DATE_PART('MONTH', u.accession_date) = :month AND DATE_PART('YEAR', u.accession_date) = :year", nativeQuery = true)
    int findByAccessionDate(int month, int year);

    @Query(value = "SELECT * FROM user_credentials u WHERE (DATE_PART('MONTH', u.accession_date) <= :month AND DATE_PART('YEAR', u.accession_date) <= :year) OR (DATE_PART('MONTH', u.accession_date) > :month AND DATE_PART('YEAR', u.accession_date) < :year)", nativeQuery = true)
    List<UserCredentials> usersByDate(int month, int year);

}
