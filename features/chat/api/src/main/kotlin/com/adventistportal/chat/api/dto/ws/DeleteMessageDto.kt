package com.adventistportal.chat.api.dto.ws

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId

data class DeleteMessageDto(
  val chatId: ChatId,
  val messageId: ChatMessageId
)