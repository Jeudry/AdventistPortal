package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Serializable

enum class IncomingWebSocketMessageType {
    NEW_MESSAGE
}

enum class OutgoingWebSocketMessageType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    ERROR
}

@Serializable
data class IncomingWebsocketMessage(
    val type: IncomingWebSocketMessageType,
    val payload: String
)

@Serializable
data class OutgoingWebsocketMessage(
    val type: OutgoingWebSocketMessageType,
    val payload: String
)