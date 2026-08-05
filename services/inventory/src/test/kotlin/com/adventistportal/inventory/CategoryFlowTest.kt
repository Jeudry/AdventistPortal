package com.adventistportal.inventory

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * How the register is organised, which until now was unreachable.
 *
 * The controller, the service, the repository and the table all existed; the schema
 * declared none of it, and Spring GraphQL ignores a controller method with no field to
 * match — silently, which is why nobody noticed. These are the first calls the category
 * API has ever received.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class CategoryFlowTest {

    @LocalServerPort private var port: Int = 0

    @Test
    fun `a category is created, listed and read back`() {
        val name = "Sonido-${UUID.randomUUID().toString().take(8)}"

        val id = createCategory(name, description = "Todo lo del salón")

        val one = graphql("""query { category(id: "$id") { name description isActive } }""")
        assertTrue(one.contains(name), "reading it back should give the name: $one")
        assertTrue(one.contains("Todo lo del salón"), "and the description: $one")

        assertTrue(graphql("query { categories { id name } }").contains(name), "and it should be listed")
    }

    @Test
    fun `a category can sit under another, and each knows the other`() {
        val parentName = "Mobiliario-${UUID.randomUUID().toString().take(8)}"
        val childName = "Sillas-${UUID.randomUUID().toString().take(8)}"

        val parentId = createCategory(parentName)
        val childId = createCategory(childName, parentId = parentId)

        val child = graphql("""query { category(id: "$childId") { name parent { name } } }""")
        assertTrue(child.contains(parentName), "the child should point at its parent: $child")

        val parent = graphql("""query { category(id: "$parentId") { name children { name } } }""")
        assertTrue(parent.contains(childName), "and the parent should list its children: $parent")
    }

    @Test
    fun `a category is renamed and retired`() {
        val id = createCategory("Provisional-${UUID.randomUUID().toString().take(8)}")
        val newName = "Definitivo-${UUID.randomUUID().toString().take(8)}"

        graphql(
            """mutation { updateCategory(input: {
                id: "$id", name: "$newName", isActive: false
            }) }""",
        )

        val updated = graphql("""query { category(id: "$id") { name isActive } }""")
        assertTrue(updated.contains(newName), "the new name should stick: $updated")
        assertTrue(updated.contains("\"isActive\":false"), "and so should being retired: $updated")
    }

    @Test
    fun `a deleted category is gone from the register`() {
        val name = "Efimera-${UUID.randomUUID().toString().take(8)}"
        val id = createCategory(name)

        graphql("""mutation { deleteCategory(id: "$id") }""")

        assertFalse(graphql("query { categories { name } }").contains(name), "it should no longer be listed")
    }

    @Test
    fun `an anonymous caller cannot read the register`() {
        val response = RestClient.create("http://localhost:$port")
            .post().uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .body("""{"query":"{ categories { id } }"}""")
            .retrieve().onStatus({ true }, { _, _ -> })
            .toBodilessEntity()

        assertTrue(response.statusCode.is4xxClientError, "the register is not public")
    }

    private fun createCategory(name: String, description: String? = null, parentId: String? = null): String {
        val fields = buildString {
            append("""name: "$name"""")
            description?.let { append(""", description: "$it"""") }
            parentId?.let { append(""", parentId: "$it"""") }
        }
        val body = graphql("mutation { createCategory(input: { $fields }) }")
        return ID.find(body)?.groupValues?.get(1) ?: error("no id returned: $body")
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

    private fun String.asJsonString() =
        "\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\""

    companion object {
        private val CALLER: UUID = UUID.fromString("00000000-0000-0000-0000-0000000000bb")
        private val ID = """"create[A-Za-z]*"\s*:\s*"([0-9a-fA-F-]{36})"""".toRegex()

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
