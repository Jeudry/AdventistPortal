package com.adventistportal.tooling

import jakarta.persistence.Entity
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Exports the *current JPA model* to a Postgres DDL script — fully offline, no database.
 *
 * This is the "model" side of the EF-Core-style migration flow: the generated
 * `model-schema.sql` is later loaded into a throwaway Postgres and diffed against the
 * state produced by the Liquibase master changelog to obtain the delta migration.
 *
 * Output file is controlled by the `modelSchemaOut` system property (see the
 * `exportModelSchema` Gradle task); defaults to `build/model-schema.sql`.
 */
class ModelSchemaExportTest {

    @Test
    fun exportModelSchema() {
        val out = File(System.getProperty("modelSchemaOut", "build/model-schema.sql"))
        out.parentFile?.mkdirs()
        if (out.exists()) out.delete()

        val settings = mapOf<String, Any>(
            "hibernate.dialect" to "org.hibernate.dialect.PostgreSQLDialect",
            "hibernate.physical_naming_strategy" to
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy",
            "hibernate.implicit_naming_strategy" to
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl",
            // emit CREATE SCHEMA for the *_service namespaces
            "hibernate.hbm2ddl.create_namespaces" to "true",
            // script-only generation: never touches a database
            "jakarta.persistence.schema-generation.scripts.action" to "create",
            "jakarta.persistence.schema-generation.scripts.create-target" to out.absolutePath,
        )

        val registry: StandardServiceRegistry =
            StandardServiceRegistryBuilder().applySettings(settings).build()
        try {
            val sources = MetadataSources(registry)
            val scanner = ClassPathScanningCandidateComponentProvider(false)
            scanner.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
            val entities = scanner.findCandidateComponents("com.adventistportal")
            check(entities.isNotEmpty()) { "no @Entity classes found on the classpath" }
            entities.forEach { bd ->
                sources.addAnnotatedClass(Class.forName(bd.beanClassName))
            }
            val metadata = sources.buildMetadata()
            // Runs schema generation with script action only (no DB connection).
            SchemaManagementToolCoordinator.process(metadata, registry, settings) { /* no delayed drop */ }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry)
        }

        assertTrue(out.exists() && out.readText().isNotBlank(), "model schema DDL was not generated")
        println("[exportModelSchema] wrote ${out.absolutePath} (${out.length()} bytes)")
    }
}
