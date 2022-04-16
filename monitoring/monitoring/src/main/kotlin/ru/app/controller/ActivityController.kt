package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.UserDTO
import ru.app.exceptions.BadRequestException
import ru.app.exceptions.TokenExpiredException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Activity
import ru.app.services.ActivityService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class ActivityController(
    private val tokenService: TokenService,
    private val activityService: ActivityService,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/my/activity")
    fun getMyActivity(@RequestParam token: String): List<Activity> {
        log.info("GET: /api/v1/my/activity")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        return activityService.getUserActivity(me)
    }

    @PostMapping("/api/v1/user/activity")
    fun getUserActivity(@RequestParam token: String, @RequestBody userDTO: UserDTO): List<Activity> {
        log.info("POST: /api/v1/user/activity")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        val interest = if (userDTO.login != null) userService.getUser(userDTO.login) else throw BadRequestException()

        if (me.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            return activityService.getUserActivity(interest)
        }
        throw BadRequestException()
    }

    @GetMapping("/api/v1/team/activity")
    fun getMyTeamActivity(@RequestParam token: String): List<Activity> {
        log.info("GET: /api/v1/team/activity")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        return activityService.getTeamActivity(userService.getWorkersAsUser(me.user_id!!))
    }

    @PostMapping("/api/v1/team/activity")
    fun getTeamActivity(@RequestParam token: String, @RequestBody userDTO: UserDTO): List<Activity> {
        log.info("POST: /api/v1/team/activity")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        val interest = if (userDTO.login != null) userService.getUser(userDTO.login) else throw BadRequestException()

        if (interest != null && (me.permissions == "admin" || userService.isBoss(me.user_id!!, interest.user_id!!)))
            return activityService.getTeamActivity(userService.getWorkersAsUser(interest.user_id!!))
        throw BadRequestException()
    }

    @GetMapping("/api/v1/activity/start")
    fun startActivity(@RequestParam token: String): String {
        log.info("GET: /api/v1/activity/start")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        activityService.startActivity(me)
        return "OK"
    }

    @GetMapping("/api/v1/activity/end")
    fun endActivity(@RequestParam token: String): String {
        log.info("GET: /api/v1/activity/end")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        activityService.endActivity(me)
        return "OK"
    }


}