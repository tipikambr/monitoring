package ru.app.controller

import org.springframework.web.bind.annotation.*
import ru.app.dto.UserDTO
import ru.app.exceptions.*
import ru.app.model.Token
import ru.app.model.User
import ru.app.services.TokenService
import ru.app.services.UserService
import ru.app.utils.passwordHash

@RestController
class UserController(
    private val userService: UserService,
    private val tokenService: TokenService
) {

    @GetMapping("/api/v1/info")
    fun info(@RequestParam token: String): UserDTO {
        val id = tokenService.checkToken(token) ?: throw UnauthorizedAccessException()
        return userService.getUserDTO(id) ?: throw UserNotFoundException()
    }

    @PostMapping("/api/v1/info")
    fun info(@RequestParam token: String, @RequestBody user: UserDTO): UserDTO {
        val myId = tokenService.checkToken(token) ?: throw UnauthorizedAccessException()
        val me = userService.getUser(myId)
        val interest = userService.getUser(user.login!!)
        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            return userService.getUserDTO(interest.login)!!
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/register")
    fun register(@RequestBody userDTO: UserDTO): Token {
        var registeredUser = userService.getUser(userDTO.login!!)
        if (registeredUser == null) {
            registeredUser = userService.register(userDTO)
            return tokenService.generateToken(registeredUser.user_id!!, registeredUser.login, userDTO.password!!)
        }
        throw UserAlreadyExistsException()
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody loginPassword: UserDTO): Token {
        val login = loginPassword.login
        val user = userService.getUser(login!!) ?: throw UserNotFoundException()
        val password = passwordHash(loginPassword.password!!)
        if (user.password == password) {
            val token = tokenService.checkToken(user.user_id!!)
            if (token != null)
                return token
            return tokenService.generateToken(user.user_id!!, user.login, user.password)
        }
        throw UserAccessException()
    }

    @PostMapping("/api/v1/delete/user")
    fun delete(@RequestParam token: String, @RequestBody user: UserDTO): String {
        val myID = tokenService.checkToken(token) ?: throw UnauthorizedAccessException()
        val interest = userService.getUser(user.login ?: throw UserNotFoundException())
        val me = userService.getUser(myID)
        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            userService.deleteUser(interest.login)
            return "OK"
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/update/user")
    fun update(@RequestParam token: String, @RequestBody user: UserDTO): String {
        val myID = tokenService.checkToken(token) ?: throw UnauthorizedAccessException()
        val interest = userService.getUser(user.login ?: throw UserNotFoundException())
        val me = userService.getUser(myID)
        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            userService.updateUser(
                User(
                    interest.user_id,
                    user.user_name ?: interest.user_name,
                    interest.login,
                    if (user.password != null) passwordHash(user.password) else interest.password,
                    if (user.company_name != null) userService.getCompanyByName(user.company_name).company_id else interest.company_id,
                    user.hours ?: interest.hours,
                    user.permissions ?: interest.permissions,
                    if (user.boss_login != null) userService.getUser(user.boss_login)!!.user_id else interest.boss_id
                )
            )
            return "OK"
        }
        throw PermissionDeniedException()
    }
}