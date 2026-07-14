package com.adventistportal.chat.domain.models

import com.adventistportal.core.domain.types.UserId

data class ChatParticipant(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null,
)
