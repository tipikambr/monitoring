package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.User

interface UserRepository : CrudRepository<User, String> {
    @Query("SELECT * FROM users")
    fun getUsers(): List<User>

    @Query("SELECT * FROM users WHERE login = :user_login LIMIT 1")
    fun getUser(@Param("user_login")login: String): User?

    @Query("SELECT * FROM users WHERE user_id = :user_id LIMIT 1")
    fun getUser(@Param("user_id")id: Long): User?

    @Modifying
    @Query("DELETE FROM users WHERE login = :login")
    fun deleteByLogin(@Param("login") login: String)

    @Query("SELECT COUNT(*) FROM users WHERE boss_id = :boss_id")
    fun hasWorkers(@Param("boss_id") id: Long): Int
}