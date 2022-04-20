package ru.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table

@Table("firebase")
data class FirebaseModel(
    @JsonProperty("user_id") val user_id: Long?,
    @JsonProperty("token") val token: String
)

