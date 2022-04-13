package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.model.Token
import ru.app.services.TokenService

@RestController
class TokenController(
    private val tokenService: TokenService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/refreshToken")
    fun refresh(@RequestParam refresh_token: String): Token {
        log.info("GET: /api/v1/refreshToken")
        return tokenService.updateToken(refresh_token!!)
    }
}