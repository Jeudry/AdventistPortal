package com.adventistportal.chat

import com.adventistportal.contracts.user.v1.GetUserRequest
import com.adventistportal.contracts.user.v1.GetUserResponse
import com.adventistportal.contracts.user.v1.User
import com.adventistportal.contracts.user.v1.UsersGrpc
import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertEquals

/**
 * What the one synchronous call in the system is for.
 *
 * Participants here are a projection of `UserEvent.Created`. When that event never
 * arrives — this service rebuilt from an empty database, or the message dead-lettered
 * after failing — the projection has a hole and nothing replays events to fill it.
 * Without a way to ask, creating a chat with that person fails forever.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(properties = ["adventistportal.users.grpc-target=localhost:19090"])
class ParticipantRepairTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a participant no event ever delivered is fetched and kept`() {
        val creator = UUID.randomUUID()
        val other = UUID.randomUUID()
        knownToUserService(creator, "recovered")
        knownToUserService(other, "the-other")
        locallyPresent(other)

        assertEquals(0, participantsFor(creator), "the event for the creator never arrived")

        val response = createChat(by = creator, with = other)

        assertEquals(HttpStatus.OK, response.statusCode, "the chat is created anyway: ${response.body}")
        assertEquals(1, participantsFor(creator), "and the hole in the projection is filled")
    }

    @Test
    fun `a participant the user service has never heard of is still refused`() {
        val ghost = UUID.randomUUID()
        val other = UUID.randomUUID()
        knownToUserService(other, "present")
        locallyPresent(other)

        val response = createChat(by = ghost, with = other)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode, "an empty projection was right this time")
        assertEquals(0, participantsFor(ghost))
    }

    private fun createChat(by: UUID, with: UUID) = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, by.toString())
        .build()
        .post().uri("/api/v1/chat")
        .contentType(MediaType.APPLICATION_JSON)
        .body("""{"otherUsersId":["$with"]}""")
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    /** Straight into the table, standing in for an event that did arrive. */
    private fun locallyPresent(userId: UUID) {
        db.sql(
            "insert into chat_service.chat_participants (user_id, username, email, created_at) " +
                "values (?, ?, ?, now())",
        ).params(userId, "local-$userId".take(20), "$userId@adventistportal.local").update()
    }

    private fun participantsFor(userId: UUID): Int = db
        .sql("select count(*) from chat_service.chat_participants where user_id = ?")
        .param(userId).query(Int::class.java).single()

    private fun knownToUserService(userId: UUID, username: String) {
        users[userId.toString()] = User.newBuilder()
            .setUserId(userId.toString())
            .setEmail("$username@adventistportal.local")
            .setUsername(username)
            .build()
    }

    companion object {
        private val users = mutableMapOf<String, User>()

        /** A stand-in for the user service: this test is about chat's half of the call. */
        @JvmStatic
        private val userService: Server = ServerBuilder.forPort(19090)
            .addService(object : UsersGrpc.UsersImplBase() {
                override fun getUser(request: GetUserRequest, observer: StreamObserver<GetUserResponse>) {
                    val response = GetUserResponse.newBuilder()
                        .apply { users[request.userId]?.let { user = it } }
                        .build()
                    observer.onNext(response)
                    observer.onCompleted()
                }
            })
            .build()
            .start()

        @AfterAll
        @JvmStatic
        fun stopUserService() {
            userService.shutdownNow()
        }

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
