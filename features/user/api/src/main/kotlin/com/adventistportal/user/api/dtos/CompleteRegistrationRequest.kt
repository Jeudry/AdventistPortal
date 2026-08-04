package com.adventistportal.user.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

data class CompleteRegistrationRequest @JsonCreator constructor(
    @JsonProperty("token")
    val token: String,
    @field:Length(min = 1, max = 255, message = "First name must be between 1 and 255 characters.")
    @JsonProperty("firstName")
    val firstName: String,
    @field:Length(min = 1, max = 255, message = "Last name must be between 1 and 255 characters.")
    @JsonProperty("lastName")
    val lastName: String,
)
