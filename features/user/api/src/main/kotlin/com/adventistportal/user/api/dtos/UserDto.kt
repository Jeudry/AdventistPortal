package com.adventistportal.user.api.dtos

import com.adventistportal.core.domain.types.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
)
