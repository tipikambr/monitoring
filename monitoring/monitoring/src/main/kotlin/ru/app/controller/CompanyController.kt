package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.TokenExpiredException
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

    @GetMapping("/api/v1/all/company")
    fun getAll(@RequestParam token: String): List<Company> {
        log.info("GET: /api/v1/all/company")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        return companyService.getCompanies()
    }

    @GetMapping("/api/v1/info/company")
    fun info(@RequestParam token: String): Company {
        log.info("GET: /api/v1/info")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        return companyService.getCompanyById(me.company_id!!)
    }

    @PostMapping("/api/v1/register/company")
    fun register(@RequestParam token: String, @RequestBody company: Company): String {
        log.info("POST: /api/v1/register/company")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        companyService.createCompany(company)
        return "OK"
    }

    @PostMapping("/api/v1/update/company")
    fun update(@RequestParam token: String, @RequestBody company: Company): String {
        log.info("POST: /api/v1/register/company")
        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        companyService.updateCompany(company)
        return "OK"
    }

    @PostMapping("/api/v1/delete/company")
    fun delete(@RequestParam token: String, @RequestBody name: Company): String {
        log.info("POST: /api/v1/delete/company")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        val company = companyService.getCompanyByName(name.company_name)
        companyService.deleteCompany(company)
        return "OK"
    }
}