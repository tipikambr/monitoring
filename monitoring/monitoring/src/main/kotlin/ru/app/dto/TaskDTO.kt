package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import ru.app.exceptions.StatusNotExistsException
import java.sql.Timestamp

data class TaskDTO (
    @JsonProperty("task_id") val task_id: Long,
    @JsonProperty("creator_login") val creator_login: String?,
    @JsonProperty("project_name") val project_name: String,
    @JsonProperty("task_name") val task_name: String,
    @JsonProperty("task_description") val task_description: String?,
    @JsonProperty("start_time") val start_time: Timestamp?,
    @JsonProperty("end_time") val end_time: Timestamp?,
    @JsonProperty("status") val status: Status?,
    @JsonProperty("progress") val progress: String?,
    @JsonProperty("worker_login") val worker_login: String? = null
    )

enum class Status (val status: String?) {
    NEW("NEW"),
    PROGRESS("PROGRESS"),
    APPROVING("APPROVING"),
    REJECTED("REJECTED"),
    FINISHED("FINISHED");
}

fun setStatus(status: String?): Status? {
    return when(status) {
        Status.NEW.status -> Status.NEW
        Status.PROGRESS.status -> Status.PROGRESS
        Status.APPROVING.status -> Status.APPROVING
        Status.REJECTED.status -> Status.REJECTED
        Status.FINISHED.status -> Status.FINISHED
        null -> null
        else -> throw StatusNotExistsException()
    }
}