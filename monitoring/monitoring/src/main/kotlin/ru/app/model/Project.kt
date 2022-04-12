package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table

@Table
@JsonIgnoreProperties(
    "project_id",
    "company_id"
)
data class Project (
    @JsonProperty("project_id") val project_id: Long,
    @JsonProperty("company_id") val company_id: Long,
    @JsonProperty("project_name") val project_name: String,
    @JsonProperty("project_description") val project_description: String
)
