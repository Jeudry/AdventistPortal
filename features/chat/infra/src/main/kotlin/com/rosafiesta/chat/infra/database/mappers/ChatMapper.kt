package com.adventistportal.chat.infra.database.mappers

import com.adventistportal.chat.domain.models.Chat
import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.chat.infra.database.entities.ChatEntity

fun ChatEntity.toModel(lastMessage: ChatMessage? = null): Chat {
    return Chat(
        id = id!!,
        participants = participants.map {
            it.toModel()
        }.toSet(),
        creator = creator!!.toModel(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = createdAt,
        lastMessage = lastMessage
    )
}