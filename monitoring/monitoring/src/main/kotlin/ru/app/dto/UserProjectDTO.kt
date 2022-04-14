package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserProjectDTO (
    @JsonProperty("new_user_login") val new_user_login: String,
    @JsonProperty("project_name") val project_name: String,
    @JsonProperty("project_creator_login") val project_creator_login: String?
)