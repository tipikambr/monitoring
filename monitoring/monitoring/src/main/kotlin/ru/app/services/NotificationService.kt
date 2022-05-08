package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.NotificationDTO
import ru.app.model.Notification
import ru.app.model.User
import ru.app.repository.GeolocationRepository
import ru.app.repository.NotificationRepository
import ru.app.repository.UserRepository
import java.sql.Timestamp
import java.util.*

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
){
    fun addNotification(me: User, user: User, notification: NotificationDTO) {
        notificationRepository.addUserNotification(
            user.user_id!!,
            notification.notification,
            me.login,
            Timestamp(Date().time),
            null
        )
    }

    fun getUserNotifications(user: User): List<Notification> {
        return notificationRepository.getUserNotifications(user.user_id!!)
    }

    fun updateNotification(me: User, notification: NotificationDTO) {
        notificationRepository.updateUserNotification(
            notification.id,
            me.user_id!!,
            Timestamp(Date().time)
        )
    }

}