package com.adventistportal.core.domain.events.chat

import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.UserId
import java.time.Instant
import java.util.*

sealed class ChatEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = ChatEventConstants.CHAT_EXCHANGE,
    override val occurredAt: Instant = Instant.now()
): AdventistPortalEvent {
    data class NewMessage(
        val senderId: UserId,
        val senderUsername: String,
        val recipientIds: Set<UserId>,
        val chatId: ChatId,
        val message: String,
        override val eventKey: String = ChatEventConstants.CHAT_NEW_MESSAGE
    ): ChatEvent(), AdventistPortalEvent


}