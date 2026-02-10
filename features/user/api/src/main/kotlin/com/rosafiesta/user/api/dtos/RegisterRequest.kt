package com.rosafiesta.user.api.dtos

import com.rosafiesta.user.api.utils.Password
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class RegisterRequest @JsonCreator constructor(
    @field:Email("Must be a valid email address")
    @JsonProperty("email")
    val email: String,
    @field:Length(min = 3, max = 255, message = "Username must be between 3 and 255 characters.")
    @JsonProperty("username")
    val username: String,
    @field:Password
    @JsonProperty("password")
    val password: String,
)