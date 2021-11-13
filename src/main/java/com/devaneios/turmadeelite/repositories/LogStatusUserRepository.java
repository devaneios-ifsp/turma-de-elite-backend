package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.LogStatusUser;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogStatusUserRepository extends PagingAndSortingRepository<LogStatusUser,Long> {

    @Query(value = "SELECT old_is_active FROM log_status_user WHERE user_id = :user_id ORDER BY date_action DESC LIMIT 1", nativeQuery = true)
    Boolean statusUserDate(long user_id);

    @Query("SELECT count(u.id) FROM LogStatusUser u WHERE u.old_is_active=FALSE AND DATE_PART('MONTH', u.date_action) = :month AND DATE_PART('YEAR', u.date_action) = :year")
    int countActiveUsers(int month, int year);

    @Query("SELECT count(u.id) FROM LogStatusUser u WHERE u.old_is_active=TRUE AND DATE_PART('MONTH', u.date_action) = :month AND DATE_PART('YEAR', u.date_action) = :year")
    int countInactiveUsers(int month, int year);

    @Modifying
    @Query(value = "INSERT INTO log_status_user(user_id, date_action, old_is_active) VALUES (:user_id, CURRENT_DATE, :old_is_active);",nativeQuery = true)
    void insertLogStatusUser(Long user_id, Boolean old_is_active);

}
