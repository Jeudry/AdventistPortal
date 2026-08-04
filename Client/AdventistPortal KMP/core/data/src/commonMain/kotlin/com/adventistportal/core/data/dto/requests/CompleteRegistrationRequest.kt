package com.adventistportal.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class CompleteRegistrationRequest(
    val token: String,
    val firstName: String,
    val lastName: String,
)
