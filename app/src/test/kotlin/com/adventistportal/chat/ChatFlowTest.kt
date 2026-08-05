package com.adventistportal.chat

import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.core.domain.events.user.UserEventConstants
import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
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
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Chat from the outside, over the two contracts it actually has: user events arriving on
 * the bus, and REST calls carrying the identity the gateway asserts.
 *
 * Both are what an extraction breaks, and neither is exercised by anything else.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ChatFlowTest {

    @Autowired private lateinit var rabbitTemplate: RabbitTemplate
    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a user created elsewhere becomes a chat participant here`() {
        val userId = announceUser("arrives")

        assertEquals(1, participantsFor(userId))
    }

    @Test
    fun `a chat is visible to both its participants and to nobody else`() {
        val creator = announceUser("creator")
        val invited = announceUser("invited")
        val stranger = announceUser("stranger")

        val chatId = createChat(by = creator, with = listOf(invited))

        assertTrue(chatsOf(creator).contains(chatId.toString()), "the creator sees the chat")
        assertTrue(chatsOf(invited).contains(chatId.toString()), "the invited participant sees it")
        assertTrue(!chatsOf(stranger).contains(chatId.toString()), "a stranger does not")
    }

    @Test
    fun `a participant who leaves stops seeing the chat`() {
        val creator = announceUser("leaver-creator")
        val invited = announceUser("leaver")

        val chatId = createChat(by = creator, with = listOf(invited))
        assertEquals(HttpStatus.OK, call(invited).delete().uri("/api/chat/$chatId/leave").exchangeStatus())

        assertTrue(!chatsOf(invited).contains(chatId.toString()), "the chat is gone for whoever left")
        assertTrue(chatsOf(creator).contains(chatId.toString()), "and still there for whoever stayed")
    }

    @Test
    fun `a request with no asserted identity is refused`() {
        val anonymous = RestClient.create("http://localhost:$port")
            .get().uri("/api/chat")
            .retrieve().onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertEquals(HttpStatus.UNAUTHORIZED, anonymous.statusCode)
    }

    /** Sends the event the user service sends, and waits for chat to have acted on it. */
    private fun announceUser(name: String): UUID {
        val userId = UUID.randomUUID()
        rabbitTemplate.convertAndSend(
            UserEventConstants.USER_EXCHANGE,
            UserEventConstants.USER_CREATED_KEY,
            UserEvent.Created(userId = userId, email = "$name@adventistportal.local", username = name),
        )
        await().atMost(Duration.ofSeconds(20)).until { participantsFor(userId) == 1 }
        return userId
    }

    private fun createChat(by: UUID, with: List<UUID>): UUID {
        val body = call(by).post()
            .uri("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"otherUsersId":[${with.joinToString(",") { "\"$it\"" }}]}""")
            .retrieve().onStatus({ true }, { _, _ -> })
            .body(String::class.java)
            .orEmpty()

        return UUID.fromString(
            ID.find(body)?.groupValues?.get(1) ?: error("no chat id in the response: $body"),
        )
    }

    private fun chatsOf(userId: UUID): String = call(userId)
        .get().uri("/api/chat")
        .retrieve().onStatus({ true }, { _, _ -> })
        .body(String::class.java)
        .orEmpty()

    private fun call(userId: UUID) = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, userId.toString())
        .build()

    private fun participantsFor(userId: UUID): Int = db
        .sql("select count(*) from chat_service.chat_participants where user_id = ?")
        .param(userId).query(Int::class.java).single()

    private fun RestClient.RequestHeadersSpec<*>.exchangeStatus() =
        retrieve().onStatus({ true }, { _, _ -> }).toBodilessEntity().statusCode

    companion object {
        private val ID = """"id"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()

        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")

        @Container
        @ServiceConnection
        @JvmStatic
        val rabbit = RabbitMQContainer("rabbitmq:4-alpine")

        @Container
        @ServiceConnection(name = "redis")
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine").withExposedPorts(6379)
    }
}
