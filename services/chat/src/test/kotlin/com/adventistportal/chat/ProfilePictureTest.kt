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
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Profile pictures, and the only third party this system talks to.
 *
 * Supabase is unreachable here, deliberately: the point is what the service does when
 * somebody else's server is down, which is the part nothing established. The paths that
 * do not need it are exercised as well — confirming a first picture never calls out,
 * because there is no previous file to remove.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(properties = ["supabase.url=http://127.0.0.1:1"])
class ProfilePictureTest {

    @Autowired private lateinit var db: JdbcClient
    @LocalServerPort private var port: Int = 0

    @Test
    fun `a first picture is confirmed without asking the third party anything`() {
        val userId = participant("first-picture")
        val url = "http://127.0.0.1:1/storage/v1/object/public/avatars/$userId.png"

        val response = confirm(userId, url)

        assertEquals(HttpStatus.OK, response.statusCode, "confirm failed: ${response.body}")
        assertEquals(url, pictureOf(userId))
    }

    @Test
    fun `a picture hosted anywhere else is refused`() {
        val userId = participant("elsewhere")

        val response = confirm(userId, "https://un-sitio-cualquiera.example/avatar.png")

        assertTrue(response.statusCode.isError, "the URL is stored and served to other people")
        assertNull(pictureOf(userId), "and nothing should have been written")
    }

    @Test
    fun `removing a picture while the third party is down leaves it alone`() {
        val userId = participant("cannot-remove")
        val url = "http://127.0.0.1:1/storage/v1/object/public/avatars/$userId.png"
        confirm(userId, url)

        val response = remove(userId)

        // The file and the row have to agree. Failing loudly and changing nothing is the
        // right answer here: the alternative is a profile pointing at a picture that is
        // still there, or a picture nobody can reach and no record of it.
        assertTrue(response.statusCode.isError, "it should not report success it did not achieve")
        assertEquals(url, pictureOf(userId), "and the row should be untouched")
    }

    @Test
    fun `asking for an upload while the third party is down fails rather than inventing a url`() {
        val userId = participant("no-credentials")

        val response = client(userId)
            .post().uri("/api/v1/participants/profile-picture-upload?mimeType=image/png")
            .retrieve().onStatus({ true }, { _, _ -> })
            .toEntity(String::class.java)

        assertTrue(response.statusCode.isError, "a signed URL cannot be made up locally")
    }

    private fun confirm(userId: UUID, url: String) = client(userId)
        .post().uri("/api/v1/participants/confirm-profile-picture")
        .contentType(MediaType.APPLICATION_JSON)
        .body("""{"publicUrl":"$url"}""")
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun remove(userId: UUID) = client(userId)
        .delete().uri("/api/v1/participants/profile-picture")
        .retrieve().onStatus({ true }, { _, _ -> })
        .toEntity(String::class.java)

    private fun client(userId: UUID) = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, userId.toString())
        .build()

    private fun pictureOf(userId: UUID): String? = db
        .sql("select profile_picture_url from chat_service.chat_participants where user_id = ?")
        .param(userId).query(String::class.java).optional().orElse(null)

    private fun participant(name: String): UUID {
        val userId = UUID.randomUUID()
        db.sql(
            "insert into chat_service.chat_participants (user_id, username, email, created_at) " +
                "values (?, ?, ?, now())",
        ).params(userId, "$name-${userId.toString().take(4)}", "$userId@adventistportal.local").update()
        return userId
    }

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
