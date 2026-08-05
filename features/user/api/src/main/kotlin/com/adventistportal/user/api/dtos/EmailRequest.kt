package com.adventistportal.user.api.dtos

import kotlinx.serialization.Serializable

import com.adventistportal.user.api.utils.Password
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Serializable
data class EmailRequest(
    @field:Email val email: String,
)
