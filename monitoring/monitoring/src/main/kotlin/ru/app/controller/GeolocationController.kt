package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.ProjectInfo
import ru.app.dto.UserDTO
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.TokenExpiredException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Geolocation
import ru.app.services.CompanyService
import ru.app.services.GeolocationService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class GeolocationController(
    private val tokenService: TokenService,
    private val userService: UserService,
    private val geolocationService: GeolocationService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/api/v1/geolocation/get")
    fun getUserGeolocation(@RequestParam token: String, @RequestBody user : UserDTO): List<Geolocation> {
        log.info("POST: /api/v1/geolocation/get")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        val user = userService.getUser(user.login!!) ?: throw UserNotFoundException()

        return geolocationService.getUserGeolocation(user)
    }


    @PostMapping("/api/v1/geolocation/send")
    fun saveGeolocation(@RequestParam token: String, @RequestBody geolocation : Geolocation): String {
        log.info("POST: /api/v1/geolocation/send")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!

        geolocationService.addGeolocationRepository(me, geolocation)
        return "OK"
    }
}