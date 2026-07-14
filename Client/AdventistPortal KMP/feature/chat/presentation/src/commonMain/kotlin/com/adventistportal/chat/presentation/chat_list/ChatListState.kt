package com.adventistportal.chat.presentation.chat_list

import com.adventistportal.chat.presentation.model.ChatUi
import com.adventistportal.core.designsystem.components.avatar.ChatParticipantUi
import com.adventistportal.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
)