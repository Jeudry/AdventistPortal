package com.adventistportal.chat.domain.events

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.UserId

data class ChatCreatedEvent(
  val chatId: ChatId,
  val participantIds: List<UserId>,
)