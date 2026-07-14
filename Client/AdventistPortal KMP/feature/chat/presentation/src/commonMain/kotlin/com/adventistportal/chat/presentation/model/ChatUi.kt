package com.adventistportal.chat.presentation.model

import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.chat.domain.models.ChatParticipant
import com.adventistportal.core.designsystem.components.avatar.ChatParticipantUi
import kotlin.time.Instant

data class ChatUi(
    val id: String,
    val localParticipant: ChatParticipantUi,
    val otherParticipants: List<ChatParticipantUi>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderUsername: String?
)
