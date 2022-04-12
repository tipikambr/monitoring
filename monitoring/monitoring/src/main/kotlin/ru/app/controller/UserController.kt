package ru.app.controller

import org.springframework.web.bind.annotation.*
import ru.app.dto.UserDTO
import ru.app.exceptions.*
import ru.app.model.Token
import ru.app.services.UserService
import ru.app.utils.passwordHash

@RestController
class UserController(val service: UserService) {
    @GetMapping("/api/v1/info")
    fun info(@RequestParam token: String): UserDTO {
        val id = service.checkToken(token) ?: throw UnauthorizedAccessException()
        return service.getUserDTO(id) ?: throw UserNotFoundException()
    }

    @PostMapping("/api/v1/info")
    fun info(@RequestParam token: String, @RequestBody user: UserDTO): UserDTO {
        val myId = service.checkToken(token) ?: throw UnauthorizedAccessException()
        val me = service.getUser(myId)
        val interest = service.getUser(user.login!!)
        if (me!!.permissions == "admin" || (interest != null && service.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            return service.getUserDTO(interest.login)!!
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/register")
    fun register(@RequestBody userDTO: UserDTO): Token {
        var registeredUser = service.getUser(userDTO.login!!)
        if (registeredUser == null) {
            registeredUser = service.register(userDTO)
            return service.generateToken(registeredUser.user_id!!, registeredUser.login, userDTO.password!!)
        }
        throw UserAlreadyExistsException()
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody loginPassword: UserDTO): Token {
        val login = loginPassword.login
        val user = service.getUser(login!!) ?: throw UserNotFoundException()
        val password = passwordHash(loginPassword.password!!)
        if (user.password == password) {
            val token = service.checkToken(user.user_id!!)
            if (token != null)
                return token
            return service.generateToken(user.user_id!!, user.login, user.password)
        }
        throw UserAccessException()
    }

    @PostMapping("/api/v1/delete/user")
    fun delete(@RequestParam token: String, @RequestBody user: UserDTO): String {
        val myID = service.checkToken(token) ?: throw UnauthorizedAccessException()
        val interest = service.getUser(user.login ?: throw UserNotFoundException())
        val me = service.getUser(myID)
        if (me!!.permissions == "admin" || (interest != null && service.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            service.deleteUser(interest.login)
            return "OK"
        }
        throw PermissionDeniedException()
    }
}