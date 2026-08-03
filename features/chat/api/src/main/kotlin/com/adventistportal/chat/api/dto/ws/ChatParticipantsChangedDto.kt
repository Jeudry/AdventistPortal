package com.adventistportal.chat.api.dto.ws

import com.adventistportal.core.domain.types.ChatId

data class ChatParticipantsChangedDto(
  val chatId: ChatId
)