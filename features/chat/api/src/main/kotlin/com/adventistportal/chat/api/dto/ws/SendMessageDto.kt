package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId

@Serializable
data class SendMessageDto(
  @Contextual val messageId: ChatMessageId? = null,
  val content: String,
  @Contextual val chatId: ChatId? = null
)
