package com.adventistportal.notification

import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.core.domain.events.user.UserEventConstants
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID
import kotlin.test.assertNotNull

/**
 * What happens to an event this service cannot handle.
 *
 * There is no SMTP server here, so every send fails. The event must survive that: it used
 * to be swallowed, which acknowledged the message and left an e-mail nobody received with
 * nothing anywhere saying so.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(properties = ["spring.mail.host=127.0.0.1", "spring.mail.port=1"])
class DeadLetterTest {

    @Autowired private lateinit var rabbitTemplate: RabbitTemplate

    @Test
    fun `an event that cannot be handled ends up in the dead-letter queue`() {
        rabbitTemplate.convertAndSend(
            UserEventConstants.USER_EXCHANGE,
            UserEventConstants.USER_REGISTRATION_STARTED,
            UserEvent.RegistrationStarted(
                registrationId = UUID.randomUUID(),
                email = "nowhere@adventistportal.local",
                username = "nowhere",
                verificationToken = "token",
            ),
        )

        // Three deliveries with backoff, then rejection. Kept, not dropped: an event that
        // cannot be handled is something to go and look at.
        await().atMost(Duration.ofSeconds(60)).untilAsserted {
            assertNotNull(deadLettered(), "the event should be waiting in the dead-letter queue")
        }
    }

    private fun deadLettered(): Message? =
        rabbitTemplate.receive("notification.user.events.dlq")

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").withInitScript("provision-schema.sql")

        @Container
        @ServiceConnection
        @JvmStatic
        val rabbit = RabbitMQContainer("rabbitmq:4-alpine")
    }
}
