package com.adventistportal.user.api.dtos

import kotlinx.serialization.Serializable

import org.hibernate.validator.constraints.Length

@Serializable
data class CompleteRegistrationRequest(
    val token: String,
    @field:Length(min = 1, max = 255, message = "First name must be between 1 and 255 characters.")
    val firstName: String,
    @field:Length(min = 1, max = 255, message = "Last name must be between 1 and 255 characters.")
    val lastName: String,
)
