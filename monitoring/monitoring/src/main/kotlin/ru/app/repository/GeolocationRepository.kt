package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Geolocation
import java.sql.Timestamp

interface GeolocationRepository : CrudRepository<Geolocation, String> {

    @Query("SELECT * FROM geolocation WHERE user_id = :user_id")
    fun getUserGeolocation(@Param("user_id") user_id: Long): List<Geolocation>

    @Modifying
    @Query("INSERT INTO geolocation VALUES (:user_id, :latitude, :longitude, :altitude, :time_update)")
    fun saveGeolocation(
        @Param("user_id") user_id: Long,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("altitude") altitude: Double,
        @Param("time_update") time_update: Timestamp,

    )
}