package ru.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class Notification (
    @JsonProperty("notification_id") val notification_id: Long,
    @JsonProperty("user_id") val user_id: Long?,
    @JsonProperty("notification") val notification: String?,
    @JsonProperty("from_login") val from_login: String?,
    @JsonProperty("when_time") val when_time: Timestamp?,
    @JsonProperty("approved") val approved: Timestamp?
)