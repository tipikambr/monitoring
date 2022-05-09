package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PhotoDTO (
    @JsonProperty("user_login") val userLogin: String,
    @JsonProperty("photo") val photo: String
)