package com.adventistportal.user.auth

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import com.adventistportal.core.services.JwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * The rest of what an account can do: stay logged in, and recover or change a password.
 *
 * Every one of these was reachable and untested. They are the endpoints an attacker
 * reaches for first, which is a poor reason to leave them uncovered.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class PasswordAndSessionTest {

    @Autowired private lateinit var db: JdbcClient
    @Autowired private lateinit var jwtService: JwtService
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a refresh token buys a new access token`() {
        val account = register("refreshes")

        val refreshed = post("/api/v1/auth/refresh", """{"refreshToken":"${account.refreshToken}"}""")

        assertEquals(HttpStatus.OK, refreshed.statusCode, "refresh failed: ${refreshed.body}")
        val newAccessToken = ACCESS_TOKEN.find(refreshed.body.orEmpty())?.groupValues?.get(1)
        assertNotNull(newAccessToken, "no access token came back: ${refreshed.body}")
        assertTrue(jwtService.validateAccessToken(newAccessToken), "and it has to be usable")
    }

    @Test
    fun `an access token is not a refresh token`() {
        val account = register("wrong-token-type")

        val refused = post("/api/v1/auth/refresh", """{"refreshToken":"${account.accessToken}"}""")

        assertEquals(HttpStatus.UNAUTHORIZED, refused.statusCode, "the type claim is what separates them")
    }

    @Test
    fun `a forgotten password is reset with the token that was issued, once`() {
        val account = register("forgets")
        assertEquals(HttpStatus.OK, post("/api/v1/auth/forgot-password", """{"email":"${account.email}"}""").statusCode)

        val token = resetTokenFor(account.email)
        val reset = post("/api/v1/auth/reset-password", """{"token":"$token","newPassword":"$NEW_PASSWORD"}""")
        assertEquals(HttpStatus.OK, reset.statusCode, "reset failed: ${reset.body}")

        assertEquals(HttpStatus.OK, login(account.email, NEW_PASSWORD).statusCode, "the new password works")
        assertEquals(HttpStatus.UNAUTHORIZED, login(account.email, PASSWORD).statusCode, "the old one does not")

        val replayed = post("/api/v1/auth/reset-password", """{"token":"$token","newPassword":"OtraMas123!"}""")
        assertTrue(replayed.statusCode.isError, "a spent reset token must not work twice")
    }

    @Test
    fun `asking to reset an address nobody registered says nothing about it`() {
        val response = post("/api/v1/auth/forgot-password", """{"email":"nobody@adventistportal.local"}""")

        // The same answer as for an address that exists. Anything else turns this endpoint
        // into a way to ask which e-mails have accounts.
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `changing a password needs the old one, and invalidates it`() {
        val account = register("changes")

        val wrongOld = postAs(
            account.userId,
            "/api/v1/auth/change-password",
            """{"oldPassword":"NoEsLaMia123!","newPassword":"$NEW_PASSWORD"}""",
        )
        assertTrue(wrongOld.statusCode.isError, "the current password has to be proved")

        val changed = postAs(
            account.userId,
            "/api/v1/auth/change-password",
            """{"oldPassword":"$PASSWORD","newPassword":"$NEW_PASSWORD"}""",
        )
        assertEquals(HttpStatus.OK, changed.statusCode, "change failed: ${changed.body}")

        assertEquals(HttpStatus.UNAUTHORIZED, login(account.email, PASSWORD).statusCode)
        assertEquals(HttpStatus.OK, login(account.email, NEW_PASSWORD).statusCode)
    }

    @Test
    fun `asking again for the verification e-mail issues a fresh token`() {
        val email = "asks-again@adventistportal.local"
        post("/api/v1/auth/register", """{"email":"$email","username":"asksagain","password":"$PASSWORD"}""")
        val first = verificationTokenFor(email)

        val resent = post("/api/v1/auth/resend-verification", """{"email":"$email"}""")

        assertEquals(HttpStatus.OK, resent.statusCode, "resend failed: ${resent.body}")
        assertNotEquals(first, verificationTokenFor(email), "the previous token is replaced, not repeated")
    }

    @Test
    fun `asking for the verification e-mail of an address nobody registered says nothing`() {
        val response = post("/api/v1/auth/resend-verification", """{"email":"nobody-here@adventistportal.local"}""")

        // Same answer as for an address that exists, for the same reason as forgot-password:
        // otherwise this is a way to ask which addresses have an account.
        assertFalse(response.statusCode.is5xxServerError, "it should not fail loudly: ${response.statusCode}")
    }

    private data class Account(
        val email: String,
        val userId: UUID,
        val accessToken: String,
        val refreshToken: String,
    )

    private fun register(name: String): Account {
        val email = "$name@adventistportal.local"
        post("/api/v1/auth/register", """{"email":"$email","username":"$name","password":"$PASSWORD"}""")

        val token = verificationTokenFor(email)
        send(HttpMethod.GET, "/api/v1/auth/verify?token=$token")
        post("/api/v1/auth/complete-registration", """{"token":"$token","firstName":"J","lastName":"P"}""")

        val body = login(email, PASSWORD).body.orEmpty()
        return Account(
            email = email,
            userId = UUID.fromString(assertNotNull(USER_ID.find(body), "no user id: $body").groupValues[1]),
            accessToken = assertNotNull(ACCESS_TOKEN.find(body), "no access token: $body").groupValues[1],
            refreshToken = assertNotNull(REFRESH_TOKEN.find(body), "no refresh token: $body").groupValues[1],
        )
    }

    private fun login(email: String, password: String) =
        post("/api/v1/auth/login", """{"email":"$email","password":"$password"}""")

    private fun verificationTokenFor(email: String): String = assertNotNull(
        db.sql(
            """
            select t.token from user_service.email_verification_tokens t
            join user_service.pending_registrations p on p.id = t.pending_registration_id
            where p.email = ? order by t.expires_at desc limit 1
            """,
        ).param(email).query(String::class.java).optional().orElse(null),
        "no verification token for $email",
    )

    private fun resetTokenFor(email: String): String = assertNotNull(
        db.sql(
            """
            select t.token from user_service.password_reset_tokens t
            join user_service.users u on u.id = t.user_id
            where u.email = ? order by t.expires_at desc limit 1
            """,
        ).param(email).query(String::class.java).optional().orElse(null),
        "no reset token for $email",
    )

    private fun post(uri: String, body: String) = send(HttpMethod.POST, uri, body)

    private fun postAs(userId: UUID, uri: String, body: String) = client(userId)
        .method(HttpMethod.POST).uri(uri)
        .headers { it.contentType = MediaType.APPLICATION_JSON }
        .body(body)
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun send(method: HttpMethod, uri: String, body: String? = null): ResponseEntity<String> =
        client(null).method(method).uri(uri)
            .headers { it.contentType = MediaType.APPLICATION_JSON }
            .let { if (body == null) it else it.body(body) }
            .retrieve().onStatus({ true }, { _, _ -> })
            .toEntity(String::class.java)

    private fun client(userId: UUID?): RestClient {
        val builder = RestClient.builder().baseUrl("http://localhost:$port")
        userId?.let { builder.defaultHeader(USER_ID_HEADER, it.toString()) }
        return builder.build()
    }

    companion object {
        private const val PASSWORD = "Secreto123!"
        private const val NEW_PASSWORD = "Secreto456!"
        private val ACCESS_TOKEN = """"accessToken"\s*:\s*"([^"]+)"""".toRegex()
        private val REFRESH_TOKEN = """"refreshToken"\s*:\s*"([^"]+)"""".toRegex()
        private val USER_ID = """"id"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()

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
