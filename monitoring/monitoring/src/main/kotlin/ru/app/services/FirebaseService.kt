package ru.app.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service
import ru.app.exceptions.TokenNotExistsException
import ru.app.model.FirebaseModel
import ru.app.model.User
import ru.app.repository.FirebaseRepository
import java.io.FileInputStream


@Service
class FirebaseService(
    private val firebaseRepository: FirebaseRepository
) {
    init {
        val serviceAccount = FileInputStream("C:/keys/keys.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun saveFirebaseToken(user: User, firebase: FirebaseModel) {
        if (firebaseRepository.getUserFirebase(user.user_id) != null)
            firebaseRepository.updateUserToken(user.user_id, firebase.token)
        else
            firebaseRepository.saveFirebase(user.user_id, firebase.token)
    }

    fun getUserFirebaseToken(user: User): FirebaseModel? {
        return firebaseRepository.getUserFirebase(user.user_id)
    }

    fun notificate(me: User, user: User, message: String?) {
        val registerToken = firebaseRepository.getUserFirebase(user.user_id)?.token ?: throw TokenNotExistsException()

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                .setTitle("message from " + me.login)
                .setBody(message)
                .build()
            ).setToken(registerToken)
            .build()

        val a = FirebaseMessaging.getInstance().send(message)
    }
}