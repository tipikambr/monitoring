package ru.app.services

import org.springframework.stereotype.Service
import ru.app.model.Token
import ru.app.repository.TokenRepository
import ru.app.utils.hashString
import java.util.*

@Service
class TokenService(private val tokenRepository: TokenRepository) {
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