package com.adventistportal.user.api.dtos

import com.adventistportal.user.api.utils.Password
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailRequest(
    @field:Email val email: String,
)
