package com.adventistportal.chat.domain.exceptions

import com.adventistportal.core.domain.types.ChatMessageId

class ChatMessageNotFoundEx(
    private val id: ChatMessageId
): RuntimeException("Chat message with id $id not found")