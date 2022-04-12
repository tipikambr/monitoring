package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
@JsonIgnoreProperties(
    "user_id",
    "password",
    "company_id",
    "boss_id"
)
data class User(
    @Id @JsonProperty("user_id") val user_id: Long?,
    @JsonProperty("user_name") val user_name: String?,
    @JsonProperty("login") val login: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("company_id") val company_id: Int?,
    @JsonProperty("hours") val hours: Int?,
    @JsonProperty("permissions") val permissions: String?,
    @JsonProperty("boss_id") val boss_id: Long?
){
    constructor(user_name: String?, login: String, password: String, company_id: Int?, hours: Int?, permissions: String?, boss_id: Long?)
            : this(null, user_name, login, password, company_id, hours, permissions, boss_id)
}
