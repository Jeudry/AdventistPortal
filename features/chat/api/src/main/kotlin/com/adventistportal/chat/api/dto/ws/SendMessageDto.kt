package com.adventistportal.chat.api.dto.ws

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId

data class SendMessageDto(
  val messageId: ChatMessageId? = null,
  val content: String,
  val chatId: ChatId? = null
)
