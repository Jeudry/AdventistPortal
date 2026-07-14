package com.adventistportal.chat.domain.events

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId

data class MessageDeletedEvent(
    val chatId: ChatId,
    val messageId: ChatMessageId
){

}