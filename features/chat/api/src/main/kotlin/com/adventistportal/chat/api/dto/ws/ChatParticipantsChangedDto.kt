package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ChatId

@Serializable
data class ChatParticipantsChangedDto(
  @Contextual val chatId: ChatId
)