package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.UserDTO
import ru.app.exceptions.UnauthorizedAccessException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Company
import ru.app.services.CompanyService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class CompanyController(
    private val tokenService: TokenService,
    private val companyService: CompanyService,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/info/company")
    fun info(@RequestParam token: String): Company {
        log.info("GET: /api/v1/info")
        val userId = tokenService.checkToken(token) ?: throw UnauthorizedAccessException()
        val me = userService.getUser(userId)!!
        return companyService.getCompanyById(me.company_id!!)
    }
}