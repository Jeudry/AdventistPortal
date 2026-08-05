package com.adventistportal.chat

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.junit.jupiter.api.Test
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
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Reading a chat, its messages, and adding or removing people.
 *
 * Every one of these decides what somebody is allowed to see, and none of them was
 * tested. They are also the endpoints where getting it wrong is quiet: showing a chat to
 * the wrong person looks exactly like showing it to the right one.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ChatMembershipTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a participant reads the chat, an outsider does not`() {
        val member = participant("reads")
        val other = participant("reads-other")
        val outsider = participant("reads-outsider")
        val chatId = createChat(by = member, with = other)

        val asMember = get(member, "/api/v1/chat/$chatId")
        assertEquals(HttpStatus.OK, asMember.statusCode)
        assertTrue(asMember.body.orEmpty().contains(chatId.toString()), "the member sees it")

        val asOutsider = get(outsider, "/api/v1/chat/$chatId")
        assertFalse(
            asOutsider.body.orEmpty().contains(chatId.toString()),
            "an outsider must not: ${asOutsider.statusCode} ${asOutsider.body}",
        )
    }

    @Test
    fun `the messages of a chat come back to a participant`() {
        val member = participant("messages")
        val other = participant("messages-other")
        val chatId = createChat(by = member, with = other)
        givenAMessage(chatId, from = member, saying = "quedó grabado")

        val messages = get(member, "/api/v1/chat/$chatId/messages").body.orEmpty()

        assertTrue(messages.contains("quedó grabado"), "the message should be listed: $messages")
    }

    @Test
    fun `a chat someone is added to shows up for them`() {
        val creator = participant("adds")
        val first = participant("adds-first")
        val invited = participant("adds-invited")
        val chatId = createChat(by = creator, with = first)

        assertFalse(chatsOf(invited).contains(chatId.toString()), "not theirs yet")

        val added = post(creator, "/api/v1/chat/$chatId/add", """{"userIds":["$invited"]}""")
        assertEquals(HttpStatus.OK, added.statusCode, "adding failed: ${added.body}")

        assertTrue(chatsOf(invited).contains(chatId.toString()), "and now it is")
    }

    @Test
    fun `an outsider cannot add themselves to a chat`() {
        val creator = participant("guard")
        val first = participant("guard-first")
        val outsider = participant("guard-outsider")
        val chatId = createChat(by = creator, with = first)

        val attempt = post(outsider, "/api/v1/chat/$chatId/add", """{"userIds":["$outsider"]}""")

        assertTrue(attempt.statusCode.isError, "membership is not something you grant yourself")
        assertFalse(chatsOf(outsider).contains(chatId.toString()))
    }

    @Test
    fun `only the sender deletes their message`() {
        val sender = participant("deletes")
        val other = participant("deletes-other")
        val chatId = createChat(by = sender, with = other)
        val messageId = givenAMessage(chatId, from = sender, saying = "me arrepiento")

        val byOther = delete(other, "/api/v1/messages/$messageId")
        assertTrue(byOther.statusCode.isError, "somebody else's message is not yours to delete")
        assertEquals(1, messagesIn(chatId))

        val bySender = delete(sender, "/api/v1/messages/$messageId")
        assertEquals(HttpStatus.OK, bySender.statusCode, "delete failed: ${bySender.body}")
        assertEquals(0, messagesIn(chatId))
    }

    /** Written straight in: this test is about reading and membership, not about sending. */
    private fun givenAMessage(chatId: UUID, from: UUID, saying: String): UUID {
        val messageId = UUID.randomUUID()
        db.sql(
            "insert into chat_service.chat_messages (id, chat_id, sender_id, content, created_at) " +
                "values (?, ?, ?, ?, now())",
        ).params(messageId, chatId, from, saying).update()
        return messageId
    }

    private fun createChat(by: UUID, with: UUID): UUID {
        val body = post(by, "/api/v1/chat", """{"otherUsersId":["$with"]}""").body.orEmpty()
        return UUID.fromString(ID.find(body)?.groupValues?.get(1) ?: error("no chat id: $body"))
    }

    private fun chatsOf(userId: UUID) = get(userId, "/api/v1/chat").body.orEmpty()

    private fun get(userId: UUID, uri: String) = client(userId)
        .get().uri(uri)
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun post(userId: UUID, uri: String, body: String) = client(userId)
        .post().uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun delete(userId: UUID, uri: String) = client(userId)
        .delete().uri(uri)
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun client(userId: UUID) = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, userId.toString())
        .build()

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
