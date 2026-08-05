package com.adventistportal.user.api.dtos

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import java.util.UUID

@Serializable
data class PendingRegistrationDto(
    @Contextual val id: UUID,
    val email: String,
    val username: String,
    val isVerified: Boolean,
)
