package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table
@JsonIgnoreProperties(
    "task_id",
    "user_id",
    "project_id",
    "creator_id"
)
data class Task (
    @Id @JsonProperty("task_id") val task_id: Long,
    @JsonProperty("user_id") val user_id: Long,
    @JsonProperty("creator_id") val creator_id: Long,
    @JsonProperty("project_id") val project_id: Long,
    @JsonProperty("task_name") val task_name: String,
    @JsonProperty("task_description") val task_description: String?,
    @JsonProperty("start_time") val start_time: Timestamp?,
    @JsonProperty("end_time") val end_time: Timestamp?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("progress") val progress: String?
)