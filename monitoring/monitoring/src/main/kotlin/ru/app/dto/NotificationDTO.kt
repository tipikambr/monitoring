package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationDTO (
    @JsonProperty("login") val login: String?,
    @JsonProperty("notification") val notification: String?
)