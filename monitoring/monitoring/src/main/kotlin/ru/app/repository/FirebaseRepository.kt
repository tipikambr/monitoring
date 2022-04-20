package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.FirebaseModel

interface FirebaseRepository : CrudRepository<FirebaseModel, String> {
    @Modifying
    @Query("INSERT INTO firebase VALUES (:user_id, :token)")
    fun saveFirebase(
        @Param("user_id")userId: Long?,
        @Param("token")token: String
    )

    @Query("SELECT * FROM firebase WHERE user_id = :user_id LIMIT 1")
    fun getUserFirebase(
        @Param("user_id")userId: Long?
    ) : FirebaseModel?

    @Modifying
    @Query("UPDATE firebase SET token = :token WHERE user_id = :user_id")
    fun updateUserToken(
        @Param("user_id")userId: Long?,
        @Param("token")token: String
    )
}