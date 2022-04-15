package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import java.sql.Timestamp

data class TaskDTO (
    @JsonProperty("creator_login") val creator_login: String,
    @JsonProperty("project_name") val project_name: String,
    @JsonProperty("task_name") val task_name: String,
    @JsonProperty("task_description") val task_description: String?,
    @JsonProperty("start_time") val start_time: Timestamp?,
    @JsonProperty("end_time") val end_time: Timestamp?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("progress") val progress: String?
)