package com.adventistportal.user.api.mappers

import com.adventistportal.user.api.dtos.AuthenticatedUserDto
import com.adventistportal.user.api.dtos.PendingRegistrationDto
import com.adventistportal.user.api.dtos.UserDto
import com.adventistportal.user.domain.model.PendingRegistration
import com.adventistportal.user.domain.model.AuthenticatedUser
import com.adventistportal.user.domain.model.User

fun AuthenticatedUser.toDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = this.user.toDto(),
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        email = this.email,
        username = this.username,
        hasEmailVerified = this.hasEmailVerified
    )
}

fun PendingRegistration.toDto(): PendingRegistrationDto {
    return PendingRegistrationDto(
        id = id,
        email = email,
        username = username,
        isVerified = isVerified,
    )
}
