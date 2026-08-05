package com.adventistportal.user.outbox

import org.awaitility.Awaitility.await
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
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import kotlin.test.assertEquals

/**
 * What the outbox is for: a broker that is down must not cost an event, and must not stop
 * the work that produced it.
 *
 * The old publisher sent inside the transaction and swallowed the failure, so an outage
 * meant a user existed that nothing else was ever told about — silently, with no record
 * that anything had been lost.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class OutboxTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `an event is on its way once the relay has run`() {
        val email = "outbox-delivered@adventistportal.local"

        assertEquals(HttpStatus.OK, register(email).statusCode)

        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            assertEquals(0, pendingRecords(), "the relay should have sent everything it found")
        }
    }

    private fun register(email: String) = RestClient.create("http://localhost:$port")
        .post()
        .uri("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body("""{"email":"$email","username":"${email.substringBefore('@')}","password":"Secreto123!"}""")
        .retrieve()
        .onStatus({ true }, { _, _ -> })
        .toBodilessEntity()

    private fun pendingRecords(): Int = db
        .sql("select count(*) from user_service.outbox where sent_at is null")
        .query(Int::class.java)
        .single()

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").withInitScript("provision-schema.sql")

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

/**
 * The same registration with nothing listening on the broker's port.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(properties = ["spring.rabbitmq.host=127.0.0.1", "spring.rabbitmq.port=1"])
class OutboxSurvivesABrokerOutageTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `the registration still succeeds and the event waits in the outbox`() {
        val email = "outbox-outage@adventistportal.local"

        val response = RestClient.create("http://localhost:$port")
            .post()
            .uri("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"email":"$email","username":"outboxoutage","password":"Secreto123!"}""")
            .retrieve()
            .onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertEquals(HttpStatus.OK, response.statusCode, "an unreachable broker must not fail the request")
        assertEquals(1, registrationsFor(email), "the registration is committed")
        assertEquals(1, pendingRecords(), "and the event it produced is still owed to the bus")
    }

    private fun registrationsFor(email: String): Int = db
        .sql("select count(*) from user_service.pending_registrations where email = ?")
        .param(email).query(Int::class.java).single()

    private fun pendingRecords(): Int = db
        .sql("select count(*) from user_service.outbox where sent_at is null")
        .query(Int::class.java)
        .single()

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").withInitScript("provision-schema.sql")

        @Container
        @ServiceConnection(name = "redis")
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine").withExposedPorts(6379)
    }
}
