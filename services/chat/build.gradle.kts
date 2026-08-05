plugins {
    id("adventistportal.spring-boot-app")
    id("adventistportal.schema-export")
}
group = "com.adventistportal.chat"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal chat service"

springBoot {
    mainClass.set("com.adventistportal.chat.ChatServiceApplicationKt")
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(projects.core.domain)
    implementation(projects.core.infra)
    implementation(projects.core.api)
    implementation(projects.features.chat.domain)
    implementation(projects.features.chat.infra)
    implementation(projects.features.chat.service)
    implementation(projects.features.chat.api)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.kotlin.reflect)
    implementation(libs.liquibase.starter)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.awaitility)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}
