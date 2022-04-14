package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table

@Table
data class ProjectsUsers (
    @JsonProperty("user_id") val user_id: Long,
    @JsonProperty("project_id") val project_id: Long,
)