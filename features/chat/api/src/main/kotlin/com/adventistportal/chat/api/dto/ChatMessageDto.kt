package com.adventistportal.chat.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId
import com.adventistportal.core.domain.types.UserId
import java.time.Instant

@Serializable
data class ChatMessageDto(
    @Contextual val id: ChatMessageId,
    @Contextual val chatId: ChatId,
    val content: String,
    @Contextual val createdAt: Instant,
    @Contextual val senderId: UserId
)
