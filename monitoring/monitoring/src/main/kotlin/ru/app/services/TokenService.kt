package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.TokenExpiredException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Token
import ru.app.model.User
import ru.app.repository.TokenRepository
import ru.app.repository.UserRepository
import ru.app.utils.hashString
import java.sql.Timestamp
import java.util.*

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {
    fun generateToken(id: Long, login: String, password: String) : Token {
        val token = hashString("SHA-224", login + password + Date())
        val refresh_token = hashString("SHA-256", login + password + Date())

        val now = Date().time
        val tokenEndtime = Timestamp(now + 3600 * 1000L)
        val refreshTokenEndtime = Timestamp(now + 7 * 24 * 3600 * 1000L)

        tokenRepository.addToken(id, token, tokenEndtime, refresh_token, refreshTokenEndtime)
        return Token(id, token, tokenEndtime, refresh_token, refreshTokenEndtime)

    }

    fun generateToken(refreshToken: String) : Token {
        val myId = tokenRepository.checkRefreshToken(refreshToken)?.user_id ?: throw TokenExpiredException()
        val user = userRepository.getUser(myId) ?: throw UserNotFoundException()

        val token = hashString("SHA-224", user.login + Date())
        val refresh_token = hashString("SHA-256", user.login + Date())

        val now = Date().time
        val tokenEndtime = Timestamp(now + 3600 * 1000L)
        val refreshTokenEndtime = Timestamp(now + 7 * 24 * 3600 * 1000L)

        tokenRepository.addToken(user.user_id!!, token, tokenEndtime, refresh_token, refreshTokenEndtime)
        return Token(user.user_id, token, tokenEndtime, refresh_token, refreshTokenEndtime)
    }

    fun updateToken(id: Long): Token {
        val user = userRepository.getUser(id) ?: throw UserNotFoundException()

        val token = hashString("SHA-224", user.login + Date())
        val refresh_token = hashString("SHA-256", user.login + Date())

        val now = Date().time
        val tokenEndtime = Timestamp(now + 3600 * 1000L)
        val refreshTokenEndtime = Timestamp(now + 7 * 24 * 3600 * 1000L)

        tokenRepository.updateToken(user.user_id!!, token, tokenEndtime, refresh_token, refreshTokenEndtime)
        return Token(user.user_id, token, tokenEndtime, refresh_token, refreshTokenEndtime)
    }

    fun updateToken(refreshToken: String): Token {
        val myId = tokenRepository.checkRefreshToken(refreshToken)?.user_id ?: throw TokenExpiredException()
        val user = userRepository.getUser(myId) ?: throw UserNotFoundException()

        val token = hashString("SHA-224", user.login + Date())
        val refresh_token = hashString("SHA-256", user.login + Date())

        val now = Date().time
        val tokenEndtime = Timestamp(now + 3600 * 1000L)
        val refreshTokenEndtime = Timestamp(now + 7 * 24 * 3600 * 1000L)

        tokenRepository.updateToken(user.user_id!!, token, tokenEndtime, refresh_token, refreshTokenEndtime)
        return Token(user.user_id, token, tokenEndtime, refresh_token, refreshTokenEndtime)
    }

    fun checkToken(token: String) : Long? {
        return tokenRepository.checkToken(token, Timestamp(Date().time))?.user_id
    }

    fun isExpired(id: Long): Boolean {
        return tokenRepository.isExists(id) != null
    }

    fun checkToken(id: Long) : Token? {
        return tokenRepository.checkToken(id)
    }
}