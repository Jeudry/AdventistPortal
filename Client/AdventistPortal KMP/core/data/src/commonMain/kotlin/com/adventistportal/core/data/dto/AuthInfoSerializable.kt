package com.adventistportal.core.data.dto

import com.adventistportal.core.domain.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthInfoSerializable(
    val accessToken: String,
    val refreshToken: String,
    val user: UserSerializable
)
