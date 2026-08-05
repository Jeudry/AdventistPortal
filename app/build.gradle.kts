
plugins {
  id("adventistportal.spring-boot-app")
}
group = "com.adventistportal"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal API backend"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

springBoot {
  mainClass.set("com.adventistportal.AdventistPortalApiApplicationKt")
}
dependencies {
  // Core modules
  implementation(projects.core.domain)
  implementation(projects.core.infra)
  implementation(projects.core.service)
  implementation(projects.core.api)
  // User modules
  implementation(projects.features.user.domain)
  implementation(projects.features.user.infra)
  implementation(projects.features.user.service)
  implementation(projects.features.user.api)
  // Chat modules
  implementation(projects.features.chat.domain)
  implementation(projects.features.chat.infra)
  implementation(projects.features.chat.service)
  implementation(projects.features.chat.api)
  // Article
  implementation(projects.features.inventory.domain)
  implementation(projects.features.inventory.infra)
  implementation(projects.features.inventory.service)
  implementation(projects.features.inventory.api)
  
  implementation(libs.spring.boot.starter.data.redis)
  implementation(libs.spring.boot.starter.amqp)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.graphql)
  
  // GraphQL Extended Scalars
  implementation("com.graphql-java:graphql-java-extended-scalars:22.0")
  
  // Jackson 3 (managed by the Spring Boot BOM); java.time is built into
  // jackson-databind in Jackson 3, so no jsr310 module is needed.
  implementation(libs.jackson.module.kotlin)

  implementation(libs.kotlin.reflect)
  implementation(libs.liquibase.starter)
  implementation(libs.springdoc.openapi.starter.webmvc.ui)
  runtimeOnly(libs.postgresql)
}

// Exports the current JPA model to a Postgres DDL script (offline, no DB) for the
// EF-style migration diff flow. See ModelSchemaExportTest and docs/migrations.md.
tasks.register<Test>("exportModelSchema") {
  group = "migrations"
  description = "Export the JPA model to build/model-schema.sql (offline, no database)."
  testClassesDirs = sourceSets["test"].output.classesDirs
  classpath = sourceSets["test"].runtimeClasspath
  useJUnitPlatform()
  filter { includeTestsMatching("com.adventistportal.tooling.ModelSchemaExportTest") }
  systemProperty(
    "modelSchemaOut",
    layout.buildDirectory.file("model-schema.sql").get().asFile.absolutePath,
  )
  outputs.upToDateWhen { false }
}