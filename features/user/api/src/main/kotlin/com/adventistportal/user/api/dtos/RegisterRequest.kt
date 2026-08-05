package com.adventistportal.user.api.dtos

import kotlinx.serialization.Serializable

import com.adventistportal.user.api.utils.Password
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

@Serializable
data class RegisterRequest(
    @field:Email("Must be a valid email address")
    val email: String,
    @field:Length(min = 3, max = 255, message = "Username must be between 3 and 255 characters.")
    val username: String,
    @field:Password
    val password: String,
)