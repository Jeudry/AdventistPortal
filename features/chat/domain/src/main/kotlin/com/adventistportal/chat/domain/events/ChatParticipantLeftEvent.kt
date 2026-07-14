package com.adventistportal.chat.domain.events

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.UserId

data class ChatParticipantLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)
