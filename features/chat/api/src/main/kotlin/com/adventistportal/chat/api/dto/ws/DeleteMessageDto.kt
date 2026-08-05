package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId

@Serializable
data class DeleteMessageDto(
  @Contextual val chatId: ChatId,
  @Contextual val messageId: ChatMessageId
)