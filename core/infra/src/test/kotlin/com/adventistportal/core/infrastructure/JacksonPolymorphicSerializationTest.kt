package com.adventistportal.core.infrastructure

import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.domain.events.user.UserEvent
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Verifies the Jackson 3 polymorphic serialization used by RabbitMqConfig and
 * RedisConfig actually round-trips sealed events (with java.time + UUID) while
 * preserving the concrete subtype via default typing.
 */
class JacksonPolymorphicSerializationTest {

    private fun mapper(): JsonMapper {
        val validator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(AdventistPortalEvent::class.java)
            .allowIfSubType("java.util.")
            .allowIfSubType("kotlin.collections.")
            .allowIfSubType("com.adventistportal.")
            .build()

        return JsonMapper.builder()
            .findAndAddModules()
            .activateDefaultTyping(validator, DefaultTyping.NON_FINAL)
            .build()
    }

    @Test
    fun `mapper round-trips a polymorphic sealed event preserving the concrete subtype`() {
        val mapper = mapper()
        val event: AdventistPortalEvent = UserEvent.Created(
            userId = UUID.randomUUID(),
            email = "test@adventistportal.com",
            username = "tester",
            verificationToken = "tok-123",
        )

        // Serialize with the base type as the root (as the Redis serializer does)
        // so default typing embeds the concrete type id.
        val json = mapper.writerFor(AdventistPortalEvent::class.java).writeValueAsString(event)
        assertTrue(json.contains("UserEvent"), "expected embedded type id, got: $json")

        val restored = mapper.readValue(json, AdventistPortalEvent::class.java)

        assertIs<UserEvent.Created>(restored)
        assertEquals(event, restored)
        assertEquals((event as UserEvent.Created).userId, restored.userId)
        // Note: occurredAt/eventId are declared on the sealed parent's constructor
        // with Instant.now()/UUID defaults (not on the data class), so they are
        // regenerated on deserialization — pre-existing design, identical under
        // Jackson 2. The business fields (data class equals) round-trip correctly.
    }

    @Test
    fun `mapper round-trips a polymorphic event inside a collection`() {
        val mapper = mapper()
        val events: List<AdventistPortalEvent> = listOf(
            UserEvent.Verified(
                userId = UUID.randomUUID(),
                email = "a@adventistportal.com",
                username = "a",
            ),
            UserEvent.RequestResetPassword(
                userId = UUID.randomUUID(),
                email = "b@adventistportal.com",
                username = "b",
                verificationToken = "tok",
                expiresInMinutes = 30,
            ),
        )

        val json = mapper.writeValueAsString(events)
        val restored: List<*> = mapper.readValue(json, List::class.java)

        assertEquals(2, restored.size)
        assertIs<UserEvent.Verified>(restored[0])
        assertIs<UserEvent.RequestResetPassword>(restored[1])
        assertEquals(events, restored)
    }

    @Test
    fun `rabbitmq JacksonJsonMessageConverter round-trips an event end-to-end`() {
        // Mirrors RabbitMqConfig.messageConverter() (trusted packages = "*").
        val converter = JacksonJsonMessageConverter(mapper(), "*").apply {
            typePrecedence = JacksonJavaTypeMapper.TypePrecedence.TYPE_ID
        }

        val event: AdventistPortalEvent = UserEvent.Created(
            userId = UUID.randomUUID(),
            email = "e2e@adventistportal.com",
            username = "e2e",
            verificationToken = "tok-xyz",
        )

        val message = converter.toMessage(event, MessageProperties())
        val restored = converter.fromMessage(message)

        assertIs<UserEvent.Created>(restored)
        assertEquals(event, restored)
    }
}
