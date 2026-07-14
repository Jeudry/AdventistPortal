package com.adventistportal.chat.domain.exceptions

import com.adventistportal.core.domain.types.UserId

class ChatParticipantNotFoundEx(
    private val id: UserId
): RuntimeException("Chat participant with id $id not found")