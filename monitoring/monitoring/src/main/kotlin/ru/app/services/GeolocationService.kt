package ru.app.services

import org.springframework.stereotype.Service
import ru.app.model.Geolocation
import ru.app.model.User
import ru.app.repository.GeolocationRepository

@Service
class GeolocationService(
    private val geolocationRepository: GeolocationRepository
) {
    fun addGeolocationRepository(user: User, geolocationDTO: Geolocation) {
        geolocationRepository.saveGeolocation(
            user.user_id!!,
            geolocationDTO.latitude,
            geolocationDTO.longitude,
            geolocationDTO.altitude,
            geolocationDTO.timeUpdate
        )
    }

    fun getUserGeolocation(user: User): List<Geolocation> {
        return geolocationRepository.getUserGeolocation(user.user_id!!)
    }
}