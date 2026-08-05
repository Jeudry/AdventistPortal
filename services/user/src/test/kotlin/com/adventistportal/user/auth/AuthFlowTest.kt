package com.adventistportal.user.auth

import com.adventistportal.core.services.JwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpMethod
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The registration and login flow against real Postgres, RabbitMQ and Redis.
 *
 * These containers are created and destroyed by the test; nothing on the developer's
 * machine is touched. The point of running the real thing is that this is the flow about
 * to be pulled out into its own process, and a mocked version of it would keep passing
 * through exactly the mistakes that extraction can make.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthFlowTest {

    /** Read through SQL, not the repositories: what matters is the row that was committed. */
    @Autowired private lateinit var db: JdbcClient
    @Autowired private lateinit var jwtService: JwtService
    @LocalServerPort private var port: Int = 0

    private val client: RestClient get() = RestClient.create("http://localhost:$port")

    @Test
    fun `a registration becomes an account only after the e-mail is confirmed and the details arrive`() {
        val email = "happy-path@adventistportal.local"

        assertEquals(HttpStatus.OK, register(email).statusCode)
        val token = verificationTokenFor(email)

        assertEquals(HttpStatus.OK, verify(token).statusCode)
        assertEquals(HttpStatus.OK, completeRegistration(token).statusCode)

        val accessToken = login(email).accessToken()
        assertTrue(jwtService.validateAccessToken(accessToken), "login must return a usable access token")
    }

    @Test
    fun `an account that has not completed registration cannot log in`() {
        val email = "not-completed@adventistportal.local"
        register(email)
        verify(verificationTokenFor(email))

        assertEquals(HttpStatus.UNAUTHORIZED, login(email).statusCode)
    }

    @Test
    fun `the wrong password is refused`() {
        val email = "wrong-password@adventistportal.local"
        completeRegistrationFor(email)

        assertEquals(HttpStatus.UNAUTHORIZED, login(email, password = "OtraCosa123!").statusCode)
    }

    @Test
    fun `a verification token cannot complete a second registration`() {
        val email = "replayed-token@adventistportal.local"
        val token = completeRegistrationFor(email)

        assertTrue(completeRegistration(token).statusCode.isError, "a spent token must not be usable again")
    }

    /**
     * Guards the rule that used to be dead: `permitAll` on the whole auth prefix matched
     * first, so this endpoint was public and only survived because its handler happens to
     * read the caller id.
     */
    @Test
    fun `change-password is not public even though it sits under the auth prefix`() {
        val response = client.post()
            .uri("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"oldPassword":"Secreto123!","newPassword":"Secreto456!"}""")
            .retrieve()
            .onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `an unverified registration holds no account`() {
        val email = "unverified@adventistportal.local"
        register(email)

        assertNull(verifiedAtFor(email), "the e-mail was never confirmed")
        assertEquals(0, accountsFor(email), "no account exists until registration is completed")
    }

    private fun completeRegistrationFor(email: String): String {
        register(email)
        val token = verificationTokenFor(email)
        verify(token)
        completeRegistration(token)
        return token
    }

    private fun verificationTokenFor(email: String): String = assertNotNull(
        db.sql(
            """
            select t.token from user_service.email_verification_tokens t
            join user_service.pending_registrations p on p.id = t.pending_registration_id
            where p.email = ? order by t.expires_at desc limit 1
            """,
        ).param(email).query(String::class.java).optional().orElse(null),
        "no verification token issued for $email",
    )

    private fun verifiedAtFor(email: String): Any? = db
        .sql("select verified_at from user_service.pending_registrations where email = ?")
        .param(email).query(java.time.Instant::class.java).optional().orElse(null)

    private fun accountsFor(email: String): Int = db
        .sql("select count(*) from user_service.users where email = ?")
        .param(email).query(Int::class.java).single()

    private fun register(email: String) = send(
        HttpMethod.POST,
        "/api/v1/auth/register",
        """{"email":"$email","username":"${email.substringBefore('@')}","password":"$PASSWORD"}""",
    )

    private fun verify(token: String) = send(HttpMethod.GET, "/api/v1/auth/verify?token=$token")

    private fun completeRegistration(token: String) = send(
        HttpMethod.POST,
        "/api/v1/auth/complete-registration",
        """{"token":"$token","firstName":"Jeudry","lastName":"Perez"}""",
    )

    private fun login(email: String, password: String = PASSWORD) = send(
        HttpMethod.POST,
        "/api/v1/auth/login",
        """{"email":"$email","password":"$password"}""",
    )

    private fun send(method: HttpMethod, uri: String, body: String? = null) =
        client.method(method)
            .uri(uri)
            .headers { it.contentType = MediaType.APPLICATION_JSON }
            .let { if (body == null) it else it.body(body) }
            .retrieve()
            .onStatus({ true }, { _, _ -> })
            .toEntity(String::class.java)

    private fun org.springframework.http.ResponseEntity<String>.accessToken(): String {
        assertEquals(HttpStatus.OK, statusCode, "login failed: $body")
        return ACCESS_TOKEN.find(body.orEmpty())?.groupValues?.get(1)
            ?: error("no accessToken in login response: $body")
    }

    companion object {
        private const val PASSWORD = "Secreto123!"
        private val ACCESS_TOKEN = """"accessToken"\s*:\s*"([^"]+)"""".toRegex()

        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
            .withInitScript("provision-schema.sql")

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
