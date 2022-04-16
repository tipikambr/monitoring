package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Activity
import ru.app.model.Company
import java.sql.Timestamp

interface ActivityRepository : CrudRepository<Activity, String> {
    @Query("SELECT * FROM activity where user_id = :user_id")
    fun getAll(@Param("user_id") userId: Long?): List<Activity>

    @Query("INSERT INTO activity (user_id, start_time) VALUES (:user_id, :start_time) RETURNING activity_id")
    fun startActivity(@Param("user_id") user_id: Long, @Param("start_time") start_time: Timestamp): Long

    @Modifying
    @Query("UPDATE activity SET end_time = :end_time WHERE user_id = :user_id AND activity_id = :activity_id")
    fun endActivity(
        @Param("activity_id") activity_id: Long,
        @Param("user_id") user_id: Long,
        @Param("end_time") end_time: Timestamp
    )

    @Query("SELECT * FROM activity WHERE activity_id = :activity_id AND user_id = :user_id")
    fun findUserActivity(
        @Param("activity_id") activity_id: Long,
        @Param("user_id") user_id: Long
    ): Activity?
}