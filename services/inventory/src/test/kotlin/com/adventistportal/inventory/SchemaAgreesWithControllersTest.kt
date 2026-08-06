package com.adventistportal.inventory

import org.junit.jupiter.api.Test
import org.springframework.boot.graphql.autoconfigure.GraphQlSourceBuilderCustomizer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.graphql.execution.SchemaReport
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * That every controller method has a schema field, and every field has a method.
 *
 * GraphQL ignores a controller method the schema does not declare: no error, no failure,
 * just a method nobody can call. The entire category API was unreachable that way —
 * controller, service, repository and table all present, and no way in — and the only
 * sign was a line in a startup report.
 *
 * Spring writes that report either way. Reading one is something nobody does; failing a
 * build is not.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(SchemaAgreesWithControllersTest.CaptureTheReport::class)
class SchemaAgreesWithControllersTest {

    @Test
    fun `nothing is declared without an implementation, or implemented without a declaration`() {
        val report = assertNotNull(captured, "the schema was never inspected")

        assertTrue(
            report.unmappedFields().isEmpty(),
            "these schema fields have nothing behind them: ${report.unmappedFields()}",
        )
        assertTrue(
            report.unmappedRegistrations().isEmpty(),
            "these controller methods can never be called: ${report.unmappedRegistrations().keys}",
        )
    }

    @TestConfiguration
    class CaptureTheReport {
        @Bean
        fun captureSchemaReport() = GraphQlSourceBuilderCustomizer { builder ->
            builder.inspectSchemaMappings { report -> captured = report }
        }
    }

    companion object {
        private var captured: SchemaReport? = null

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
