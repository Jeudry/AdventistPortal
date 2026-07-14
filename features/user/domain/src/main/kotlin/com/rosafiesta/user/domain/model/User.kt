package com.adventistportal.user.domain.model

import com.adventistportal.core.domain.types.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean,
)
