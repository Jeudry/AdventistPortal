package com.adventistportal.user.api.dtos

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.UserId

@Serializable
data class UserDto(
    @Contextual val id: UserId,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
)
