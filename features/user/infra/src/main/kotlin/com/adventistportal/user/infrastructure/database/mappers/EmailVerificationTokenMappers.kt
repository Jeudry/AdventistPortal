package com.adventistportal.user.infrastructure.database.mappers

import com.adventistportal.user.domain.model.EmailVerificationToken
import com.adventistportal.user.infrastructure.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toModel(): EmailVerificationToken {
    return EmailVerificationToken(
        id = id,
        token = token,
        registrationId = pendingRegistration.id!!,
        email = pendingRegistration.email,
        username = pendingRegistration.username,
        isVerified = pendingRegistration.isVerified,
    )
}
