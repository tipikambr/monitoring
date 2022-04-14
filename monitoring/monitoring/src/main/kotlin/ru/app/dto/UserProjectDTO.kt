package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserProjectDTO (
    @JsonProperty("user_login") val user_login: String,
    @JsonProperty("project_name") val project_name: String,
    @JsonProperty("project_creator_login") val project_creator_login: String?
)