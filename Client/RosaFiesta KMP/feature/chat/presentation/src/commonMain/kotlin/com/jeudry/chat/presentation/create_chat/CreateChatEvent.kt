package com.adventistportal.chat.presentation.create_chat

import com.adventistportal.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat): CreateChatEvent
}