package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table
@JsonIgnoreProperties(
    "activity_id",
    "user_id"
)
data class Activity (
    @Id @JsonProperty("activity_id") val activity_id: Long,
    @JsonProperty("user_id") val user_id: Long,
    @JsonProperty("start_time") val start_time: Timestamp,
    @JsonProperty("end_time") val end_time: Timestamp
)