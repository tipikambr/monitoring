package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Token
import ru.app.model.User
import java.sql.Timestamp
import java.util.*

interface TokenRepository : CrudRepository<Token, String>{
    @Modifying
    @Query("INSERT INTO tokens (user_id, token, token_endtime, refresh_token, refresh_token_endtime) VALUES (:user_id, :token, :token_endtime, :refresh_token, :refresh_token_endtime)")
    fun addToken(
        @Param("user_id") user_id: Long,
        @Param("token") token: String,
        @Param("token_endtime") token_endtime: Timestamp,
        @Param("refresh_token") refresh_token: String,
        @Param("refresh_token_endtime") refresh_token_endtime: Timestamp
    )

    @Modifying
    @Query("UPDATE tokens SET token = :token, token_endtime = :token_endtime, refresh_token= :refresh_token, refresh_token_endtime = :refresh_token_endtime WHERE user_id = :user_id")
    fun updateToken(
        @Param("user_id") user_id: Long,
        @Param("token") token: String,
        @Param("token_endtime") token_endtime: Timestamp,
        @Param("refresh_token") refresh_token: String,
        @Param("refresh_token_endtime") refresh_token_endtime: Timestamp
    )

    @Query("SELECT * FROM tokens WHERE token = :token AND token_endtime > :now")
    fun checkToken(@Param("token") token: String, @Param("now") time: Timestamp = Timestamp(Date().time)): Token?

    @Query("SELECT * FROM tokens WHERE refresh_token = :refreshToken AND refresh_token_endtime > :now LIMIT 1")
    fun checkRefreshToken(@Param("refreshToken") refreshToken: String, @Param("now") time: Timestamp = Timestamp(Date().time)): Token?

    @Query("SELECT * FROM tokens WHERE user_id = :id AND token_endtime > :now")
    fun checkToken(@Param("id") id: Long, @Param("now") time: Timestamp = Timestamp(Date().time)): Token?

    @Query("SELECT * FROM tokens WHERE user_id = :id")
    fun isExists(@Param("id") id: Long): Token?

}