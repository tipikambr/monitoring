package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table
@JsonIgnoreProperties(
    "user_id"
)
data class Token (
    @JsonProperty("user_id") val user_id: Long?,
    @JsonProperty("token") val token: String?,
    @JsonProperty("token_endtime") val token_endtime: Timestamp?,
    @JsonProperty("refresh_token") val refresh_token: String?,
    @JsonProperty("refresh_token_endtime") val refresh_token_endtime: Timestamp?
)