import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("rosafiesta.spring-boot-app")
}
group = "com.rosafiesta"
version = "0.0.1-SNAPSHOT"
description = "RosaFiesta API backend"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

springBoot {
  mainClass.set("com.rosafiesta.RosaFiestaApiApplicationKt")
}
tasks {
  named<BootJar>("bootJar") {
    from(project(":features:notification:infra").projectDir.resolve("src/main/resources")) {
      into("")
    }
  }
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
  // Notification modules
  implementation(projects.features.notification.domain)
  implementation(projects.features.notification.infra)
  implementation(projects.features.notification.service)
  implementation(projects.features.notification.api)
  // Article
  implementation(projects.features.inventory.domain)
  implementation(projects.features.inventory.infra)
  implementation(projects.features.inventory.service)
  implementation(projects.features.inventory.api)
  
  implementation(libs.spring.boot.starter.data.redis)
  implementation(libs.spring.boot.starter.amqp)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.security)
  
  // Jackson 2 (Para RabbitMQ y compatibilidad)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.jackson.module.kotlin)

  // INTENTO DE RESCATE: Jackson 3 Module Kotlin (Sin versión, confiando en el BOM)
  implementation("tools.jackson.module:jackson-module-kotlin")

  implementation(libs.kotlin.reflect)
  implementation(libs.flyway.postgresql)
  implementation(libs.flyway.starter)
  implementation(libs.liquibase.starter)
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation(libs.springdoc.openapi.starter.webmvc.ui)
  runtimeOnly(libs.postgresql)
}