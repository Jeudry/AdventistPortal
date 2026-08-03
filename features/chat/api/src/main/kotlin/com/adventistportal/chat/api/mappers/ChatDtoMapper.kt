package com.adventistportal.chat.api.mappers

import com.adventistportal.chat.api.dto.ChatDto
import com.adventistportal.chat.domain.models.Chat

fun Chat.toDto(): ChatDto {
    return ChatDto(
        id = id,
        participants = participants.map {
            it.toDto()
        },
        lastActivityAt = lastActivityAt,
        lastMessage = lastMessage?.toDto(),
        creator = creator.toDto(),
    )
}