package com.adventistportal.chat.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.UserId

@Serializable
data class ChatParticipantDto(
    @Contextual val id: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null,
)
