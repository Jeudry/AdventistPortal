package com.adventistportal.chat.domain.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import com.adventistportal.core.domain.types.ChatId
import java.time.Instant

/**
 * Cached in Redis, so it is serialisable. The ids and timestamps are contextual: they
 * are JVM types, and their format is decided in one place rather than per model.
 */
@Serializable
data class Chat(
    val id: @Contextual ChatId,
    val participants: Set<ChatParticipant>,
    val lastMessage: ChatMessage?,
    val creator: ChatParticipant,
    val lastActivityAt: @Contextual Instant,
    val createdAt: @Contextual Instant,
)
