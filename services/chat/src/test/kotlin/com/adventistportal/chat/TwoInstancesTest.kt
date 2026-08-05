package com.adventistportal.chat

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.assertTrue

/**
 * Two instances, and a message that has to cross between them.
 *
 * A socket is a file descriptor: it lives in the process holding it and cannot be
 * anywhere else. So with more than one instance, delivering from a local map reaches
 * whoever happens to be connected to you and silently misses everyone else — half a chat
 * sees the message and nothing reports a problem.
 *
 * This starts a second instance on its own port, connects each participant to a different
 * one, and sends from one side.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class TwoInstancesTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a message reaches a participant connected to the other instance`() {
        val sender = participant("crosses-sender")
        val receiver = participant("crosses-receiver")
        val chatId = createChat(by = sender, with = receiver)

        val here = connect(sender, port)
        val there = connect(receiver, secondInstancePort)

        here.session.sendMessage(TextMessage(newMessage(chatId, "¿me lees desde el otro proceso?")))

        // Both, deliberately. If neither arrives the recipients were never resolved; if
        // only the local one does, the other instance is not consuming the fanout. The
        // two failures look identical from one assertion.
        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            assertTrue(
                here.received.any { it.contains("¿me lees desde el otro proceso?") },
                "the sending instance should deliver to its own socket: ${here.received}",
            )
        }
        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            assertTrue(
                there.received.any { it.contains("¿me lees desde el otro proceso?") },
                "and the instance holding the other socket has to deliver it too: ${there.received}",
            )
        }

        here.session.close(CloseStatus.NORMAL)
        there.session.close(CloseStatus.NORMAL)
    }

    private fun newMessage(chatId: UUID, content: String): String {
        val payload = """{\"chatId\":\"$chatId\",\"content\":\"$content\"}"""
        return """{"type":"NEW_MESSAGE","payload":"$payload"}"""
    }

    private fun connect(userId: UUID, onPort: Int): Socket {
        val headers = WebSocketHttpHeaders().apply { add(USER_ID_HEADER, userId.toString()) }
        val collector = Collector()
        val session = StandardWebSocketClient()
            .execute(collector, headers, URI("ws://localhost:$onPort/ws/v1/chat"))
            .get()

        await().atMost(Duration.ofSeconds(15)).until { session.isOpen }
        return Socket(session, collector.messages)
    }

    private class Socket(val session: WebSocketSession, val received: ConcurrentLinkedQueue<String>)

    private class Collector : TextWebSocketHandler() {
        val messages = ConcurrentLinkedQueue<String>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            messages.add(message.payload)
        }
    }

    private fun createChat(by: UUID, with: UUID): UUID {
        val body = RestClient.builder()
            .baseUrl("http://localhost:$port")
            .defaultHeader(USER_ID_HEADER, by.toString())
            .build()
            .post().uri("/api/v1/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"otherUsersId":["$with"]}""")
            .retrieve().onStatus({ true }, { _, _ -> })
            .body(String::class.java)
            .orEmpty()

        return UUID.fromString(ID.find(body)?.groupValues?.get(1) ?: error("no chat id: $body"))
    }

    private fun participant(name: String): UUID {
        val userId = UUID.randomUUID()
        db.sql(
            "insert into chat_service.chat_participants (user_id, username, email, created_at) " +
                "values (?, ?, ?, now())",
        ).params(userId, "$name-${userId.toString().take(4)}", "$userId@adventistportal.local").update()
        return userId
    }

    companion object {
        private val ID = """"id"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()
        private const val SECOND_INSTANCE_PORT = 18081

        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").withInitScript("provision-schema.sql")

        @Container
        @ServiceConnection
        @JvmStatic
        val rabbit = RabbitMQContainer("rabbitmq:4-alpine")

        private var second: ConfigurableApplicationContext? = null

        /**
         * A genuinely separate application context, pointed at the same containers. Two
         * threads in one process would share the maps and prove nothing.
         */
        val secondInstancePort: Int
            get() {
                second ?: run {
                    second = SpringApplicationBuilder(ChatServiceApplication::class.java)
                        // The names the service's own yml reads. Anything set through
                        // `properties` lands in defaultProperties, which loses to a value
                        // in application.yml — including to its `${...}` placeholder.
                        .properties(
                            mapOf(
                                "SERVER_PORT" to SECOND_INSTANCE_PORT,
                                "DB_URL" to postgres.jdbcUrl,
                                "DB_USER" to postgres.username,
                                "DB_PASSWORD" to postgres.password,
                                "RABBITMQ_HOST" to rabbit.host,
                                "RABBITMQ_PORT" to rabbit.amqpPort,
                                "RABBITMQ_USER" to rabbit.adminUsername,
                                "RABBITMQ_PASSWORD" to rabbit.adminPassword,
                                "RABBITMQ_VHOST" to "/",
                                "SUPABASE_SERVICE_KEY" to "unused-in-tests",
                                "USER_GRPC_TARGET" to "localhost:19090",
                                "spring.cache.type" to "none",
                                // The first instance already applied them.
                                "spring.liquibase.enabled" to false,
                            ),
                        )
                        .run()
                }
                return SECOND_INSTANCE_PORT
            }

        @AfterAll
        @JvmStatic
        fun stopSecondInstance() {
            second?.close()
        }
    }
}
