package ru.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PhotoDTO (
    @JsonProperty("login") val login: String,
    @JsonProperty("photo") val photo: String
)