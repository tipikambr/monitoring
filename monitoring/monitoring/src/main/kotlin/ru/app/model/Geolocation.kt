package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table("geolocation")
@JsonIgnoreProperties(
    "user_id"
)
data class Geolocation (
    @JsonProperty("user_id") val user_id : Long?,
    @JsonProperty("latitude") val latitude: Double,
    @JsonProperty("longitude") val longitude: Double,
    @JsonProperty("altitude") val altitude: Double,
    @JsonProperty("time_update") val timeUpdate: Timestamp
)