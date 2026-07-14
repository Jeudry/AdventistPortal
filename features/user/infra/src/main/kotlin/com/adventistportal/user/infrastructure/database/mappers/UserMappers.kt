package com.adventistportal.user.infrastructure.database.mappers

import com.adventistportal.user.domain.model.User
import com.adventistportal.user.infrastructure.database.entities.UserEntity

fun UserEntity.toModel(): User {
    return User(
        id = id!!,
        email = email,
        username = username,
        hasEmailVerified = hasVerifiedEmail
    )
}
