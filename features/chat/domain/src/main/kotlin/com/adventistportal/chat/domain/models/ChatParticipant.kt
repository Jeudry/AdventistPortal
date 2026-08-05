package com.adventistportal.chat.domain.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import com.adventistportal.core.domain.types.UserId

@Serializable
data class ChatParticipant(
    val userId: @Contextual UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null,
)
