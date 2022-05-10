package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.HttpServiceException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.User
import ru.app.repository.UserRepository
import java.util.*


@Service
class PhotoService(
    private val userRepository: UserRepository
) {
    private val luxandCloudToken = "0287a2f02d8840b58d09a71d32abf2cb"
    private val luxandCloudRegisterLink = "https://api.luxand.cloud/subject"
    private val luxandCloudCheckLink = "https://api.luxand.cloud/photo/verify/"

    private val working = false

    fun savePhoto(photo: String, user: User) {
        if (!working) return

        var body = hashMapOf<String?, Any?>("name" to user.login);
        val response = HTTPService.postRequest(luxandCloudRegisterLink, body, luxandCloudToken)
        if (response!!.status != 200) {
            throw HttpServiceException()
        }

        val personId = response.body!!.getObject().getInt("id")
        userRepository.getUser(user.login)?.user_id ?: throw UserNotFoundException()
        userRepository.updateUser(
            user.user_id!!,
            user.user_name!!,
            user.login,
            user.password,
            user.company_id!!,
            user.hours,
            user.permissions!!,
            user.boss_id,
            personId.toLong(),
            photo
        )

        body = hashMapOf<String?, Any?>("photo" to photo)

        HTTPService.postRequest(
            "$luxandCloudRegisterLink/$personId", body, luxandCloudToken
        )
    }

    fun checkPhoto(photo: String, user: User): Boolean {
        if (!working) return true

        val body = hashMapOf<String?, Any?>("photo" to photo)


        val verification_response = HTTPService.postRequest(
            "$luxandCloudCheckLink${user.luxand_cloud_id}", body, luxandCloudToken
        )
        //REDO If service will work in future
        return false
    }

}