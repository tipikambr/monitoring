package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Token
import java.sql.Timestamp

interface TokenRepository : CrudRepository<Token, String>{
    @Modifying
    @Query("INSERT INTO tokens (user_id, token, token_endtime, refresh_token) VALUES (:user_id, :token, :token_endtime, :refresh_token)")
    fun addToken(
        @Param("user_id") user_id: Long,
        @Param("token") token: String,
        @Param("refresh_token") refresh_token: String,
        @Param("token_endtime") token_endtime: Timestamp? = null
    )

    @Modifying
    @Query("UPDATE tokens SET token = :token, token_endtime = :token_endtime, refresh_token= :refresh_token WHERE user_id = :user_id")
    fun updateToken(
        @Param("user_id") user_id: Long,
        @Param("token") token: String,
        @Param("refresh_token") refresh_token: String,
        @Param("token_endtime") token_endtime: Timestamp? = null
    )

    @Query("SELECT * FROM tokens WHERE token = :token")
    fun checkToken(@Param("token") token: String): Token?

    @Query("SELECT * FROM tokens WHERE user_id = :id")
    fun checkToken(@Param("id") id: Long): Token?

}