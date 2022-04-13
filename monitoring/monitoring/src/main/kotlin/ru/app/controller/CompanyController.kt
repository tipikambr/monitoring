package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import ru.app.services.TokenService

@RestController
class CompanyController(
    private val tokenService: TokenService,
    private val companyService: TokenService
) {
    private val log = LoggerFactory.getLogger(this::class.java)


}