package com.adventistportal.user.api.dtos

import kotlinx.serialization.Serializable

import com.adventistportal.user.domain.model.User

@Serializable
data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)
