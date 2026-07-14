package com.adventistportal.chat.api.dto

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId
import com.adventistportal.core.domain.types.UserId
import java.time.Instant

data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
