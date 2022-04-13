package ru.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("company")
@JsonIgnoreProperties(
    "company_id"
)
data class Company (
    @Id @JsonProperty("company_id") val company_id: Int?,
    @JsonProperty("company_name") val company_name : String,
    @JsonProperty("company_description") val company_description : String?,
    @JsonProperty("finger_needed") val finger_needed: Boolean,
    @JsonProperty("photo_needed") val photo_needed: Boolean,
    @JsonProperty("manager_needed") val manager_needed: Boolean
)
