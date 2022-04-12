package ru.app.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.app.model.User

@JsonIgnoreProperties(
    "password"
)
data class UserDTO(
    @JsonProperty("user_name") val user_name: String?,
    @JsonProperty("login") val login: String?,
    @JsonProperty("password") val password: String?,
    @JsonProperty("company_name") val company_name: String?,
    @JsonProperty("hours") val hours: Int?,
    @JsonProperty("permissions") val permissions: String?,
    @JsonProperty("boss_login") val boss_login: String?
)