package com.adventistportal.chat.infra.database.mappers

import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.chat.infra.database.entities.ChatMessageEntity

fun ChatMessageEntity.toModel(): ChatMessage {
    return ChatMessage(
        id = id!!,
        chatId = chatId!!,
        sender = sender!!.toModel(),
        content = content,
        createdAt = createdAt
    )
}