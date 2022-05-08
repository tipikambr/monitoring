package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.NotificationDTO
import ru.app.model.Notification
import ru.app.dto.UserDTO
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.TokenExpiredException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.FirebaseModel
import ru.app.services.FirebaseService
import ru.app.services.NotificationService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class FirebaseController (
    private val tokenService: TokenService,
    private val userService: UserService,
    private val firebaseService: FirebaseService,
    private val notificationService: NotificationService
){
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/api/v1/firebase/send")
    fun saveFirebaseToken(@RequestParam token: String, @RequestBody firebase : FirebaseModel): String {
        log.info("POST: /api/v1/firebase/send")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!

        firebaseService.saveFirebaseToken(me, firebase)
        return "OK"
    }


    @PostMapping("/api/test/firebase/userToken")
    fun getUserToken(@RequestParam token: String, @RequestBody user : UserDTO): FirebaseModel? {
        log.info("POST: /api/test/firebase/userToken")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        val user = userService.getUser(user.login!!) ?: throw UserNotFoundException()

        return firebaseService.getUserFirebaseToken(user)
    }

    @PostMapping("/api/v1/firebase/notification")
    fun notificateUser(@RequestParam token: String, @RequestBody notification : NotificationDTO): String {
        log.info("POST: /api/v1/firebase/notification")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "manager" && me.permissions != "admin") throw PermissionDeniedException()

        val user = userService.getUser(notification.login!!) ?: throw UserNotFoundException()

        notificationService.addNotification(me, user, notification)

        firebaseService.notificate(me, user, notification.notification)

        return "OK"
    }

    @PostMapping("/api/v1/firebase/notification/approve")
    fun approveNotification(@RequestParam token: String, @RequestBody notification : NotificationDTO): String {
        log.info("POST: /api/v1/firebase/notification/approve")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)  ?: throw UserNotFoundException()


        notificationService.updateNotification(me, notification)

        return "OK"
    }

    @PostMapping("/api/v1/user/notifications")
    fun getUserNotifications(@RequestParam token: String, @RequestBody notification : NotificationDTO): List<Notification> {
        log.info("POST: /api/v1/firebase/notification/approve")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)  ?: throw UserNotFoundException()
        if (me.permissions != "manager" && me.permissions != "admin") throw PermissionDeniedException()

        val user = userService.getUser(notification.login!!) ?: throw UserNotFoundException()

        return notificationService.getUserNotifications(user)

    }
}