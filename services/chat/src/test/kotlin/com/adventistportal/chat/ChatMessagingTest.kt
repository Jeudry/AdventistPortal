package com.adventistportal.chat

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
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
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Sending a message, which is what chat is for and what nothing tested.
 *
 * Over a real WebSocket, because every interesting part of this only exists there: the
 * handshake carries the identity, the connection remembers which chats you are in, and
 * the message reaches other people by being pushed to their sockets rather than returned
 * from a call.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ChatMessagingTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a message reaches the other participant and is kept`() {
        val sender = participant("sender")
        val receiver = participant("receiver")
        val chatId = createChat(by = sender, with = receiver)

        val senderSocket = connect(sender)
        val receiverSocket = connect(receiver)

        senderSocket.send(newMessage(chatId, "hola, ¿probando?"))

        await().atMost(TIMEOUT).until { receiverSocket.received.isNotEmpty() }
        assertTrue(
            receiverSocket.received.any { it.contains("hola, ¿probando?") },
            "the other participant should have been pushed the message: ${receiverSocket.received}",
        )
        assertEquals(1, messagesIn(chatId), "and it is kept, not only broadcast")

        senderSocket.close()
        receiverSocket.close()
    }

    @Test
    fun `a message to a chat you are not in goes nowhere`() {
        val outsider = participant("outsider")
        val member = participant("member")
        val other = participant("other-member")
        val chatId = createChat(by = member, with = other)

        val socket = connect(outsider)
        socket.send(newMessage(chatId, "let me in"))

        Thread.sleep(SETTLE_MS)
        assertEquals(0, messagesIn(chatId), "membership is checked on the connection, not taken from the message")

        socket.close()
    }

    @Test
    fun `a handshake with no asserted identity never opens`() {
        // Refused at the security chain, before the handler: the upgrade returns 401 and
        // the client never gets a session at all.
        val refused = runCatching {
            StandardWebSocketClient()
                .execute(Collector(), WebSocketHttpHeaders(), java.net.URI("ws://localhost:$port/ws/v1/chat"))
                .get()
        }

        assertTrue(refused.isFailure, "the handshake should not have completed")
        assertTrue(
            refused.exceptionOrNull()?.message?.contains("401") == true,
            "and it should be refused as unauthorised: ${refused.exceptionOrNull()?.message}",
        )
    }

    private fun newMessage(chatId: UUID, content: String): String {
        val payload = """{\"chatId\":\"$chatId\",\"content\":\"$content\"}"""
        return """{"type":"NEW_MESSAGE","payload":"$payload"}"""
    }

    private fun connect(userId: UUID): Socket {
        val headers = WebSocketHttpHeaders().apply { add(USER_ID_HEADER, userId.toString()) }
        val collector = Collector()
        val session = StandardWebSocketClient()
            .execute(collector, headers, java.net.URI("ws://localhost:$port/ws/v1/chat"))
            .get()

        // The handler loads which chats you are in when the connection opens, so a message
        // sent before that has finished is dropped for the right reason and the wrong one.
        await().atMost(TIMEOUT).until { session.isOpen }
        return Socket(session, collector.messages)
    }

    private class Socket(private val session: WebSocketSession, val received: ConcurrentLinkedQueue<String>) {
        fun send(text: String) = session.sendMessage(TextMessage(text))
        fun close() = session.close(CloseStatus.NORMAL)
    }

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

    /** Straight into the table: this test is about messages, not about how participants arrive. */
    private fun participant(name: String): UUID {
        val userId = UUID.randomUUID()
        db.sql(
            "insert into chat_service.chat_participants (user_id, username, email, created_at) " +
                "values (?, ?, ?, now())",
        ).params(userId, "$name-${userId.toString().take(4)}", "$userId@adventistportal.local").update()
        return userId
    }

    private fun messagesIn(chatId: UUID): Int = db
        .sql("select count(*) from chat_service.chat_messages where chat_id = ?")
        .param(chatId).query(Int::class.java).single()

    companion object {
        private val TIMEOUT: Duration = Duration.ofSeconds(15)
        private const val SETTLE_MS = 2500L
        private val ID = """"id"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()

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
