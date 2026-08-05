package com.adventistportal.notification

import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.core.domain.events.user.UserEventConstants
import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetup
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The service that had no tests, over the two things it does: turn events into mail, and
 * remember which devices belong to whom.
 *
 * Mail is asserted against a real SMTP server running in the test process, because "an
 * e-mail was sent" is the whole job — and the code that sends it catches its own
 * exceptions, so a mocked sender would agree with anything.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class NotificationFlowTest {

    @Autowired private lateinit var rabbitTemplate: RabbitTemplate
    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a started registration becomes a verification e-mail`() {
        val email = "verify-me@adventistportal.local"

        publish(
            UserEvent.RegistrationStarted(
                registrationId = UUID.randomUUID(),
                email = email,
                username = "verifyme",
                verificationToken = "the-token",
            ),
            UserEventConstants.USER_REGISTRATION_STARTED,
        )

        val message = awaitMessageTo(email)
        assertTrue(message.subject.contains("Verify", ignoreCase = true), "wrong subject: ${message.subject}")
        assertTrue(message.bodyContains("the-token"), "the verification link must carry the token")
    }

    @Test
    fun `a password reset request becomes its own e-mail`() {
        val email = "reset-me@adventistportal.local"

        publish(
            UserEvent.RequestResetPassword(
                userId = UUID.randomUUID(),
                email = email,
                username = "resetme",
                verificationToken = "reset-token",
                expiresInMinutes = 30,
            ),
            UserEventConstants.USER_REQUEST_RESET_PASSWORD,
        )

        val message = awaitMessageTo(email)
        assertTrue(message.subject.contains("Reset", ignoreCase = true), "wrong subject: ${message.subject}")
    }

    @Test
    fun `an event this service does not care about sends nothing`() {
        val email = "ignored@adventistportal.local"

        publish(
            UserEvent.Verified(
                registrationId = UUID.randomUUID(),
                email = email,
                username = "ignored",
            ),
            UserEventConstants.USER_VERIFIED,
        )

        Thread.sleep(SETTLE_MS)
        assertEquals(0, messagesTo(email).size, "confirming an e-mail address is not a reason to send mail")
    }

    @Test
    fun `a device is remembered for its owner and forgotten on request`() {
        val userId = UUID.randomUUID()
        val deviceToken = "fcm-${UUID.randomUUID()}"

        assertEquals(HttpStatus.OK, registerDevice(userId, deviceToken).statusCode)
        assertEquals(1, devicesFor(userId), "the device belongs to the caller the gateway asserted")

        assertEquals(HttpStatus.OK, unregisterDevice(userId, deviceToken).statusCode)
        assertEquals(0, devicesFor(userId))
    }

    @Test
    fun `one caller cannot unregister another caller's device`() {
        val owner = UUID.randomUUID()
        val stranger = UUID.randomUUID()
        val deviceToken = "fcm-${UUID.randomUUID()}"
        registerDevice(owner, deviceToken)

        unregisterDevice(stranger, deviceToken)

        assertEquals(
            1,
            devicesFor(owner),
            "a token is not a permission: knowing one must not let somebody else silence your device",
        )
    }

    @Test
    fun `registering a device without an asserted identity is refused`() {
        val response = RestClient.create("http://localhost:$port")
            .post().uri("/api/v1/notification/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"token":"fcm-anonymous","platform":"ANDROID"}""")
            .retrieve().onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    private fun publish(event: UserEvent, routingKey: String) =
        rabbitTemplate.convertAndSend(UserEventConstants.USER_EXCHANGE, routingKey, event)

    private fun awaitMessageTo(recipient: String): SentMessage {
        await().atMost(Duration.ofSeconds(20)).until { messagesTo(recipient).isNotEmpty() }
        return messagesTo(recipient).first()
    }

    private fun messagesTo(recipient: String): List<SentMessage> = greenMail.receivedMessages
        .filter { message -> message.allRecipients.any { it.toString() == recipient } }
        // The message is multipart, so the body has to be extracted rather than
        // stringified: content.toString() gives the container, not the HTML.
        .map { SentMessage(it.subject.orEmpty(), GreenMailUtil.getBody(it)) }

    private data class SentMessage(val subject: String, val body: String) {
        fun bodyContains(text: String) = body.contains(text)
    }

    private fun registerDevice(userId: UUID, deviceToken: String) = callAs(userId)
        .post().uri("/api/v1/notification/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body("""{"token":"$deviceToken","platform":"ANDROID"}""")
        .retrieve().onStatus({ true }, { _, _ -> })
        .toBodilessEntity()

    private fun unregisterDevice(userId: UUID, deviceToken: String) = callAs(userId)
        .delete().uri("/api/v1/notification/$deviceToken")
        .retrieve().onStatus({ true }, { _, _ -> })
        .toBodilessEntity()

    private fun callAs(userId: UUID) = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, userId.toString())
        .build()

    private fun devicesFor(userId: UUID): Int = db
        .sql("select count(*) from notification_service.device_tokens where user_id = ?")
        .param(userId).query(Int::class.java).single()

    companion object {
        private const val SETTLE_MS = 3000L

        @RegisterExtension
        @JvmStatic
        val greenMail: GreenMailExtension = GreenMailExtension(ServerSetup(3025, "127.0.0.1", ServerSetup.PROTOCOL_SMTP))
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication())
            .withPerMethodLifecycle(false)

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
