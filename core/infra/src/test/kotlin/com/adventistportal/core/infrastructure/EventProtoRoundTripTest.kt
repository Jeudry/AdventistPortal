package com.adventistportal.core.infrastructure

import com.adventistportal.core.domain.events.chat.ChatEvent
import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.core.infrastructure.message_queue.proto.EventProtoMapper
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Every event has to survive the wire. These go through the same bytes RabbitMQ carries,
 * so a field left out of EventProtoMapper fails here rather than in a listener that
 * silently receives a default value.
 */
class EventProtoRoundTripTest {

    private fun <T : Any> roundTrip(event: T): T where T : com.adventistportal.core.domain.events.AdventistPortalEvent {
        val bytes = EventProtoMapper.toBytes(event)
        @Suppress("UNCHECKED_CAST")
        return EventProtoMapper.fromBytes(EventProtoMapper.protoTypeOf(event), bytes) as T
    }

    @Test
    fun `registration started survives the wire`() {
        val event = UserEvent.RegistrationStarted(
            registrationId = UUID.randomUUID(),
            email = "a@adventistportal.com",
            username = "ana",
            verificationToken = "tok-123",
        )
        val restored = roundTrip(event)

        assertEquals(event.registrationId, restored.registrationId)
        assertEquals(event.email, restored.email)
        assertEquals(event.username, restored.username)
        assertEquals(event.verificationToken, restored.verificationToken)
        assertEquals(event.eventKey, restored.eventKey)
    }

    @Test
    fun `created survives the wire`() {
        val event = UserEvent.Created(
            userId = UUID.randomUUID(),
            email = "b@adventistportal.com",
            username = "beto",
        )
        val restored = roundTrip(event)

        assertEquals(event.userId, restored.userId)
        assertEquals(event.email, restored.email)
        assertEquals(event.username, restored.username)
    }

    @Test
    fun `verified survives the wire`() {
        val event = UserEvent.Verified(
            registrationId = UUID.randomUUID(),
            email = "c@adventistportal.com",
            username = "cris",
        )
        val restored = roundTrip(event)

        assertEquals(event.registrationId, restored.registrationId)
        assertEquals(event.username, restored.username)
    }

    @Test
    fun `resend verification survives the wire`() {
        val event = UserEvent.RequestResendVerification(
            registrationId = UUID.randomUUID(),
            email = "d@adventistportal.com",
            username = "dani",
            verificationToken = "tok-456",
        )
        val restored = roundTrip(event)

        assertEquals(event.registrationId, restored.registrationId)
        assertEquals(event.verificationToken, restored.verificationToken)
    }

    @Test
    fun `reset password keeps its expiry`() {
        val event = UserEvent.RequestResetPassword(
            userId = UUID.randomUUID(),
            email = "e@adventistportal.com",
            username = "eva",
            verificationToken = "tok-789",
            expiresInMinutes = 30,
        )
        val restored = roundTrip(event)

        assertEquals(event.userId, restored.userId)
        assertEquals(event.expiresInMinutes, restored.expiresInMinutes)
    }

    @Test
    fun `a new message keeps every recipient`() {
        val event = ChatEvent.NewMessage(
            senderId = UUID.randomUUID(),
            senderUsername = "ana",
            recipientIds = setOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
            chatId = UUID.randomUUID(),
            message = "hola",
        )
        val restored = roundTrip(event)

        assertEquals(event.recipientIds, restored.recipientIds)
        assertEquals(event.senderId, restored.senderId)
        assertEquals(event.chatId, restored.chatId)
        assertEquals(event.message, restored.message)
    }
}
