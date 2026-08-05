package com.adventistportal.core.infrastructure.message_queue.proto

import adventistportal.chat.v1.Events as ChatProto
import adventistportal.user.v1.Events as UserProto
import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.domain.events.chat.ChatEvent
import com.adventistportal.core.domain.events.user.UserEvent
import com.google.protobuf.Timestamp
import java.time.Instant
import java.util.UUID

/**
 * Translates between the domain events and the wire contracts in contracts/proto.
 *
 * The Kotlin sealed classes stay the in-code representation; protobuf is only the wire
 * format. That keeps the domain free of generated types and means a change to the
 * contract shows up here, at one boundary, instead of across every publisher.
 */
object EventProtoMapper {

    fun toBytes(event: AdventistPortalEvent): ByteArray = when (event) {
        is UserEvent -> event.toProto().toByteArray()
        is ChatEvent -> event.toProto().toByteArray()
        else -> error("No wire contract for ${event::class.qualifiedName}")
    }

    fun protoTypeOf(event: AdventistPortalEvent): String = when (event) {
        is UserEvent -> USER_TYPE
        is ChatEvent -> CHAT_TYPE
        else -> error("No wire contract for ${event::class.qualifiedName}")
    }

    fun fromBytes(protoType: String, bytes: ByteArray): AdventistPortalEvent = when (protoType) {
        USER_TYPE -> UserProto.UserEvent.parseFrom(bytes).toDomain()
        CHAT_TYPE -> ChatProto.ChatEvent.parseFrom(bytes).toDomain()
        else -> error("Unknown wire contract: $protoType")
    }

    const val USER_TYPE = "adventistportal.user.v1.UserEvent"
    const val CHAT_TYPE = "adventistportal.chat.v1.ChatEvent"

    private fun UserEvent.toProto(): UserProto.UserEvent {
        val builder = UserProto.UserEvent.newBuilder()
            .setEventId(eventId)
            .setOccurredAt(occurredAt.toProto())

        when (this) {
            is UserEvent.RegistrationStarted -> builder.registrationStarted =
                UserProto.RegistrationStarted.newBuilder()
                    .setRegistrationId(registrationId.toString())
                    .setEmail(email)
                    .setUsername(username)
                    .setVerificationToken(verificationToken)
                    .build()

            is UserEvent.Created -> builder.created =
                UserProto.Created.newBuilder()
                    .setUserId(userId.toString())
                    .setEmail(email)
                    .setUsername(username)
                    .build()

            is UserEvent.Verified -> builder.verified =
                UserProto.Verified.newBuilder()
                    .setRegistrationId(registrationId.toString())
                    .setEmail(email)
                    .setUsername(username)
                    .build()

            is UserEvent.RequestResendVerification -> builder.requestResendVerification =
                UserProto.RequestResendVerification.newBuilder()
                    .setRegistrationId(registrationId.toString())
                    .setEmail(email)
                    .setUsername(username)
                    .setVerificationToken(verificationToken)
                    .build()

            is UserEvent.RequestResetPassword -> builder.requestResetPassword =
                UserProto.RequestResetPassword.newBuilder()
                    .setUserId(userId.toString())
                    .setEmail(email)
                    .setUsername(username)
                    .setVerificationToken(verificationToken)
                    .setExpiresInMinutes(expiresInMinutes)
                    .build()
        }
        return builder.build()
    }

    private fun UserProto.UserEvent.toDomain(): UserEvent = when (payloadCase) {
        UserProto.UserEvent.PayloadCase.REGISTRATION_STARTED -> registrationStarted.let {
            UserEvent.RegistrationStarted(
                registrationId = UUID.fromString(it.registrationId),
                email = it.email,
                username = it.username,
                verificationToken = it.verificationToken,
            )
        }
        UserProto.UserEvent.PayloadCase.CREATED -> created.let {
            UserEvent.Created(
                userId = UUID.fromString(it.userId),
                email = it.email,
                username = it.username,
            )
        }
        UserProto.UserEvent.PayloadCase.VERIFIED -> verified.let {
            UserEvent.Verified(
                registrationId = UUID.fromString(it.registrationId),
                email = it.email,
                username = it.username,
            )
        }
        UserProto.UserEvent.PayloadCase.REQUEST_RESEND_VERIFICATION -> requestResendVerification.let {
            UserEvent.RequestResendVerification(
                registrationId = UUID.fromString(it.registrationId),
                email = it.email,
                username = it.username,
                verificationToken = it.verificationToken,
            )
        }
        UserProto.UserEvent.PayloadCase.REQUEST_RESET_PASSWORD -> requestResetPassword.let {
            UserEvent.RequestResetPassword(
                userId = UUID.fromString(it.userId),
                email = it.email,
                username = it.username,
                verificationToken = it.verificationToken,
                expiresInMinutes = it.expiresInMinutes,
            )
        }
        // A producer on a newer contract sent a payload this build does not know.
        UserProto.UserEvent.PayloadCase.PAYLOAD_NOT_SET, null ->
            error("UserEvent carried no payload this build understands")
    }

    private fun ChatEvent.toProto(): ChatProto.ChatEvent {
        val builder = ChatProto.ChatEvent.newBuilder()
            .setEventId(eventId)
            .setOccurredAt(occurredAt.toProto())

        when (this) {
            is ChatEvent.NewMessage -> builder.newMessage =
                ChatProto.NewMessage.newBuilder()
                    .setSenderId(senderId.toString())
                    .setSenderUsername(senderUsername)
                    .addAllRecipientIds(recipientIds.map { it.toString() })
                    .setChatId(chatId.toString())
                    .setMessage(message)
                    .build()
        }
        return builder.build()
    }

    private fun ChatProto.ChatEvent.toDomain(): ChatEvent = when (payloadCase) {
        ChatProto.ChatEvent.PayloadCase.NEW_MESSAGE -> newMessage.let {
            ChatEvent.NewMessage(
                senderId = UUID.fromString(it.senderId),
                senderUsername = it.senderUsername,
                recipientIds = it.recipientIdsList.map { id -> UUID.fromString(id) }.toSet(),
                chatId = UUID.fromString(it.chatId),
                message = it.message,
            )
        }
        ChatProto.ChatEvent.PayloadCase.PAYLOAD_NOT_SET, null ->
            error("ChatEvent carried no payload this build understands")
    }

    private fun Instant.toProto(): Timestamp = Timestamp.newBuilder()
        .setSeconds(epochSecond)
        .setNanos(nano)
        .build()
}
