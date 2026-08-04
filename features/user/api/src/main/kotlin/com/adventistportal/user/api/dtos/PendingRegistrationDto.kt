package com.adventistportal.user.api.dtos

import java.util.UUID

data class PendingRegistrationDto(
    val id: UUID,
    val email: String,
    val username: String,
    val isVerified: Boolean,
)
