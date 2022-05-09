package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.app.dto.PhotoDTO
import ru.app.dto.UserDTO
import ru.app.exceptions.*
import ru.app.model.Company
import ru.app.model.Token
import ru.app.model.User
import ru.app.services.PhotoService
import ru.app.services.TokenService
import ru.app.services.UserService
import ru.app.utils.passwordHash

@RestController
class UserController(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val photoService: PhotoService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/all/user")
    fun getAll(@RequestParam token: String): List<UserDTO> {
        log.info("GET: /api/v1/all/users")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        return userService.getAll()
    }

    @GetMapping("/api/v1/info/user")
    fun info(@RequestParam token: String): UserDTO {
        log.info("GET: /api/v1/info")
        val id = tokenService.checkToken(token) ?: throw TokenExpiredException()
        return userService.getUserDTO(id) ?: throw UserNotFoundException()
    }

    @PostMapping("/api/v1/info/user")
    fun info(@RequestParam token: String, @RequestBody user: UserDTO): UserDTO {
        log.info("POST: /api/v1/info")
        val myId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(myId)
        val interest = userService.getUser(user.login!!)
        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            return userService.getUserDTO(interest.login)!!
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/register/user")
    fun register(@RequestParam token: String, @RequestBody userDTO: UserDTO): Token {
        log.info("POST: /api/v1/register")
        val myId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(myId)
        if (me!!.permissions == "admin" || me.permissions == "manager") {
            var registeredUser = userService.getUser(userDTO.login!!)

            if (registeredUser == null) {
                registeredUser = userService.register(userDTO)
                return tokenService.generateToken(registeredUser.user_id!!, registeredUser.login, userDTO.password!!)
            }

            throw UserAlreadyExistsException()
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody loginPassword: UserDTO): Token {
        log.info("POST: /api/v1/login")

        val login = loginPassword.login
        val user = userService.getUser(login!!) ?: throw UserNotFoundException()
        val password = passwordHash(loginPassword.password!!)

        if (user.password == password) {
            val token = tokenService.checkToken(user.user_id!!)
            if (token != null)
                return token
            if (tokenService.isExpired(user.user_id))
                return tokenService.updateToken(user.user_id)
            return tokenService.generateToken(user.user_id, user.login, user.password)
        }

        throw LoginPasswordException()
    }

    @PostMapping("/api/v1/delete/user")
    fun delete(@RequestParam token: String, @RequestBody user: UserDTO): String {
        log.info("POST: /api/v1/delete/user")

        val myID = tokenService.checkToken(token) ?: throw TokenExpiredException()
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
        log.info("POST: /api/v1/update/user")

        val myID = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val interest = if (user.login != null) userService.getUser(user.login) else  userService.getUser(myID)
        val me = userService.getUser(myID)

        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            if (me.user_id == interest.user_id && (interest.permissions != null || interest.boss_id != null || interest.hours != null)) throw PermissionDeniedException()
            userService.updateUser(
                User(
                    interest.user_id,
                    user.user_name ?: interest.user_name,
                    interest.login,
                    if (user.password != null) passwordHash(user.password) else interest.password,
                    if (user.company_name != null) userService.getCompanyByName(user.company_name).company_id else interest.company_id,
                    user.hours ?: interest.hours,
                    user.permissions ?: interest.permissions,
                    if (user.boss_login != null) userService.getUser(user.boss_login)!!.user_id else interest.boss_id,
                    interest.luxand_cloud_id
                )
            )
            return "OK"
        }

        throw PermissionDeniedException()
    }

    @GetMapping("/api/v1/getWorkers")
    fun getWorkers(@RequestParam token: String): List<UserDTO> {
        log.info("GET: /api/v1/getWorkers")

        val myID = tokenService.checkToken(token) ?: throw TokenExpiredException()
        return userService.getWorkers(myID)
    }

    @PostMapping("/api/v1/getWorkers")
    fun getWorkers(@RequestParam token: String, @RequestBody boss: UserDTO): List<UserDTO> {
        log.info("POST: /api/v1/getWorkers")

        val myId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(myId)
        val interest = userService.getUser(boss.login!!)
        if (me!!.permissions == "admin" || (interest != null && userService.isBoss(me.user_id!!, interest!!.user_id!!))){
            if (interest == null) throw UserNotFoundException()
            return userService.getWorkers(interest.user_id!!)
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/photo/register")
    fun savePhoto(@RequestParam token: String, @RequestBody photoDTO: PhotoDTO) {
        log.info("POST: /api/v1/photo/register")
        val myId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(myId)
        if (me!!.permissions == "admin" || me.permissions == "manager") {
            var registeredUser = userService.getUser(photoDTO.userLogin) ?: throw UserNotFoundException()
            photoService.savePhoto(photoDTO.photo, registeredUser)
        }
        throw PermissionDeniedException()
    }

    @PostMapping("/api/v1/photo/check")
    fun checkPhoto(@RequestParam token: String, photoDTO: PhotoDTO) {
        log.info("POST: /api/v1/photo/check")
        val myId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(myId)
        photoService.checkPhoto(photoDTO.photo, me!!)
    }
}