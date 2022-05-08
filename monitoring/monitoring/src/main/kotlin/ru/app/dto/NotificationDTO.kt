package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class NotificationDTO (
    @JsonProperty("notification_id") val id: Long,
    @JsonProperty("login") val login: String?,
    @JsonProperty("notification") val notification: String?,
    @JsonProperty("from_login") val from: String?,
    @JsonProperty("when_time") val dateTime: Timestamp?,
    @JsonProperty("approved") val approved: Timestamp?
)