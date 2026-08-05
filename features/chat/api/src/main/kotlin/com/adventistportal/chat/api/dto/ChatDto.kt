package com.adventistportal.chat.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ChatId
import java.time.Instant

@Serializable
data class ChatDto(
    @Contextual val id: ChatId,
    val participants: List<ChatParticipantDto>,
    @Contextual val lastActivityAt: Instant,
    val lastMessage: ChatMessageDto?,
    val creator: ChatParticipantDto,
)
