package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Geolocation
import ru.app.model.Notification
import java.sql.Timestamp

interface NotificationRepository  : CrudRepository<Notification, String> {
    @Query("SELECT * FROM notifications WHERE user_id = :user_id")
    fun getUserNotifications( @Param("user_id") user_id: Long): List<Notification>

    @Modifying
    @Query("INSERT INTO notifications(user_id, notification, from_login, when_time, approved) VALUES (:user_id, :notification, :from_login, :when_time, :approved)")
    fun addUserNotification(
        @Param("user_id") user_id: Long,
        @Param("notification") notification: String?,
        @Param("from_login") from_login: String,
        @Param("when_time") when_time: Timestamp,
        @Param("approved") approved: Timestamp?
    )

    @Modifying
    @Query("UPDATE notifications SET approved = :approved WHERE user_id = :user_id AND notification_id = :notification_id")
    fun updateUserNotification(
        @Param("notification_id") notification_id: Long,
        @Param("user_id") user_id: Long,
        @Param("approved") approved: Timestamp?
    )
}