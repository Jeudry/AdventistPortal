package com.adventistportal.user.api.dtos

import com.adventistportal.user.api.utils.Password
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ResetPasswordRequest(
    @field:NotBlank val token: String,
    @field:Password
    val newPassword: String,
)
