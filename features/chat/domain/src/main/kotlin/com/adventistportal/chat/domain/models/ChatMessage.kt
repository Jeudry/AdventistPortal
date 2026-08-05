package com.adventistportal.chat.domain.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId
import java.time.Instant

@Serializable
data class ChatMessage(
    val id: @Contextual ChatMessageId,
    val chatId: @Contextual ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: @Contextual Instant
)