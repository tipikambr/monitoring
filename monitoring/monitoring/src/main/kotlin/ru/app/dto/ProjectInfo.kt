package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectInfo (
    @JsonProperty("project_name") val project_name: String?,
    @JsonProperty("project_description") val project_description: String?,
    @JsonProperty("project_creator_login") val project_creator_login: String?,
    @JsonProperty("project_company_name") val project_company_name: String?,
)