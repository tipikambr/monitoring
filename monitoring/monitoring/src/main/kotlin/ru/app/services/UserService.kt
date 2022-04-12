package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.UserDTO
import ru.app.exceptions.*
import ru.app.model.Token
import ru.app.model.User
import ru.app.repository.CompanyRepository
import ru.app.repository.TokenRepository
import ru.app.repository.UserRepository
import ru.app.utils.hashString
import ru.app.utils.passwordHash
import java.util.Date

@Service
class UserService(
    val userRepository: UserRepository,
    val companyRepository: CompanyRepository,
    val tokenRepository: TokenRepository
) {
    fun getUser(login: String): User? = userRepository.getUser(login)

    fun getUser(id: Long): User? = userRepository.getUser(id)

    fun isBoss(bossId: Long, workerId: Long): Boolean {
        if (bossId == workerId) return true
        var worker = userRepository.getUser(workerId)
        while (worker?.boss_id != null && worker.boss_id != bossId)
            if (worker.boss_id == null || worker.boss_id == 0L)
                return false else
            worker = userRepository.getUser(worker.boss_id!!)
        if (worker?.boss_id != null && worker.boss_id == bossId)
            return true
        return false
    }


    fun getUserDTO(login: String): UserDTO? {
        val user = userRepository.getUser(login) ?: return null
        if (user.company_id == null) CompanyNotExistsException()
        val comnany_name = companyRepository.getUserCompanyById(user.company_id!!)?.company_name ?: throw CompanyNotExistsException()
        var boss_login: String? = null
        if (user.boss_id != null && user.boss_id != 0L)
            boss_login = userRepository.getUser(user.boss_id)?.login ?: throw BossNotFoundException()

        return UserDTO(
            user.user_name,
            user.login,
            user.password,
            comnany_name,
            user.hours,
            user.permissions,
            boss_login
        )
    }

    fun getUserDTO(id: Long): UserDTO? {
        val user = userRepository.getUser(id) ?: return null
        val comnany_name = companyRepository.getUserCompanyById(user.company_id!!)?.company_name ?: throw CompanyNotExistsException()
        var bossLogin: String? = null
        if (user.boss_id != null)
            bossLogin = userRepository.getUser(user.boss_id)?.login ?: throw BossNotFoundException()

        return UserDTO(
            user.user_name,
            user.login,
            user.password,
            comnany_name,
            user.hours,
            user.permissions,
            bossLogin
        )
    }

    fun register(userDTO: UserDTO) : User {
        if (
            userDTO.user_name == null ||
            userDTO.company_name == null
        ) throw UserRegistrationException()

        val companyId = companyRepository.getUserCompanyByName(userDTO.company_name)?.company_id ?: throw CompanyNotExistsException()
        val passwordHash = passwordHash(userDTO.password!!)

        var bossId: Long? = null
        if (userDTO.boss_login != null){
            bossId = userRepository.getUser(userDTO.boss_login)?.user_id ?: throw BossNotFoundException()
        }

        var userDB = User(
            userDTO.user_name,
            userDTO.login!!,
            passwordHash,
            companyId,
            userDTO.hours,
            userDTO.permissions,
            bossId
        )
        userRepository.save(userDB)

        userDB = userRepository.getUser(userDB.login)!!
        return userDB
    }

    fun deleteUser(login: String) {
        val boss_id = userRepository.getUser(login)?.user_id ?: throw UserNotFoundException()
        val workersCount = userRepository.hasWorkers(boss_id)
        if (workersCount != 0)
            throw WorkersExistsException()
        userRepository.deleteByLogin(login)
    }

    fun generateToken(id: Long, login: String, password: String) : Token {
        val token = hashString("SHA-224", login + password + Date())
        val refresh_token = hashString("SHA-256", login + password + Date())
        tokenRepository.addToken(id, token, refresh_token, null)
        return Token(id, token, null, refresh_token)

    }

    fun checkToken(token: String) : Long? {
        return tokenRepository.checkToken(token)?.user_id
    }

    fun checkToken(id: Long) : Token? {
        return tokenRepository.checkToken(id)
    }
}
