package com.adventistportal.user.api.dtos

import com.adventistportal.user.domain.model.User

data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)
