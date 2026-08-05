package com.adventistportal.inventory

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The asset register over GraphQL: an article goes in and comes back out unchanged.
 *
 * The value in cents gets its own assertion. It is a Long on the wire because a decimal
 * that survives a round trip through JSON and back is not something to take on trust —
 * and a register of what the church owns is exactly where a rounding error would sit
 * unnoticed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class InventoryFlowTest {

    @LocalServerPort private var port: Int = 0

    @Test
    fun `an article survives the round trip, cents included`() {
        val sku = "SKU-${UUID.randomUUID()}"

        val created = graphql(
            """
            mutation { createArticle(input: {
                nameTemplate: "Proyector del salón",
                descriptionTemplate: "Sala principal",
                variants: [{ sku: "$sku", name: "Epson EB-2250U", stock: 1, replacementCostCents: 129999,
                             attributes: [{ key: "ubicacion", value: "Salón principal" }] }]
            }) }
            """,
        )
        val id = ID.find(created)?.groupValues?.get(1) ?: error("no id returned: $created")

        val fetched = graphql("""query { article(id: "$id") { nameTemplate variants { sku replacementCostCents attributes { key value } } } }""")

        assertTrue(fetched.contains("Proyector del salón"), "the name comes back: $fetched")
        assertTrue(fetched.contains(sku), "so does the variant: $fetched")
        assertTrue(fetched.contains("129999"), "and the cents are exact, not 1299.99: $fetched")
        assertTrue(fetched.contains("ubicacion"), "and the attributes survive the map round trip: $fetched")
    }

    @Test
    fun `an article is renamed and retired`() {
        val id = createArticle("Altavoz provisional", "SKU-${UUID.randomUUID()}")

        graphql(
            """mutation { updateArticle(input: {
                id: "$id", nameTemplate: "Altavoz JBL", isActive: false
            }) }""",
        )

        val updated = graphql("""query { article(id: "$id") { nameTemplate isActive } }""")
        assertTrue(updated.contains("Altavoz JBL"), "the new name should stick: $updated")
        assertTrue(updated.contains("\"isActive\":false"), "and so should being retired: $updated")
    }

    @Test
    fun `a deleted article is gone`() {
        val id = createArticle("Efimero", "SKU-${UUID.randomUUID()}")

        graphql("""mutation { deleteArticle(id: "$id") }""")

        val gone = graphql("""query { article(id: "$id") { nameTemplate } }""")
        assertTrue(gone.contains("\"article\":null"), "reading it back should find nothing: $gone")
    }

    @Test
    fun `an anonymous caller gets nothing back`() {
        val response = RestClient.create("http://localhost:$port")
            .post().uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"query":"{ articles { id } }"}""")
            .retrieve().onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    private fun createArticle(name: String, sku: String): String {
        val created = graphql(
            """
            mutation { createArticle(input: {
                nameTemplate: "$name",
                variants: [{ sku: "$sku", name: "$name", stock: 1, replacementCostCents: 1000 }]
            }) }
            """,
        )
        return ID.find(created)?.groupValues?.get(1) ?: error("no id returned: $created")
    }

    private fun graphql(query: String): String = RestClient.builder()
        .baseUrl("http://localhost:$port")
        .defaultHeader(USER_ID_HEADER, CALLER.toString())
        .build()
        .post().uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON)
        .body("""{"query":${query.trimIndent().asJsonString()}}""")
        .retrieve().onStatus({ true }, { _, _ -> })
        .body(String::class.java)
        .orEmpty()

    private fun String.asJsonString() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\""

    companion object {
        private val CALLER: UUID = UUID.fromString("00000000-0000-0000-0000-0000000000aa")
        private val ID = """"createArticle"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()

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
