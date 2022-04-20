package ru.app.services

import org.springframework.stereotype.Service
import ru.app.model.FirebaseModel
import ru.app.model.User
import ru.app.repository.FirebaseRepository
import ru.app.repository.GeolocationRepository

@Service
class FirebaseService (
    private val firebaseRepository: FirebaseRepository
) {
    fun saveFirebaseToken(user: User, firebase: FirebaseModel){
        if (firebaseRepository.getUserFirebase(user.user_id) != null)
            firebaseRepository.updateUserToken(user.user_id, firebase.token)
        else
            firebaseRepository.saveFirebase(user.user_id, firebase.token)
    }

    fun getUserFirebaseToken(user: User): FirebaseModel? {
        return firebaseRepository.getUserFirebase(user.user_id)
    }
}