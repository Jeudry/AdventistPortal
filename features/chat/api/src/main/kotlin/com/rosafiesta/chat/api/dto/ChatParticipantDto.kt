package com.adventistportal.chat.api.dto

import com.adventistportal.core.domain.types.UserId

data class ChatParticipantDto(
    val id: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null,
)
