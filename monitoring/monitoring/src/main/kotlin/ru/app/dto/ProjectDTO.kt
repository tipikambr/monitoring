package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectDTO (
    @JsonProperty("old_project_name") val old_project_name: String,
    @JsonProperty("project_name") val project_name: String?,
    @JsonProperty("project_description") val project_description: String?,
    @JsonProperty("project_creator_login") val project_creator_login: String?,
    @JsonProperty("old_project_creator_login") val old_project_creator_login: String?
)