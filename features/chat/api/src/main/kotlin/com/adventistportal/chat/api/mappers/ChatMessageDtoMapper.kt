package com.adventistportal.chat.api.mappers

import com.adventistportal.chat.api.dto.ChatMessageDto
import com.adventistportal.chat.domain.models.ChatMessage

fun ChatMessage.toDto(): ChatMessageDto {
    return ChatMessageDto(
        id = sender.userId,
        chatId = chatId,
        content = content,
        createdAt = createdAt,
        senderId = sender.userId
    )
}