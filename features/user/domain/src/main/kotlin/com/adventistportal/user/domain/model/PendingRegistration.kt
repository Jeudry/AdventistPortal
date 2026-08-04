package com.adventistportal.user.domain.model

import java.util.UUID

/** A registration waiting to be confirmed and completed. Not a user yet. */
data class PendingRegistration(
    val id: UUID,
    val email: String,
    val username: String,
    val isVerified: Boolean,
)
