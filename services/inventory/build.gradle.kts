plugins {
    id("adventistportal.spring-boot-app")
    id("adventistportal.schema-export")
}
group = "com.adventistportal.inventory"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal inventory service"

springBoot {
    mainClass.set("com.adventistportal.inventory.InventoryServiceApplicationKt")
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(projects.core.domain)
    implementation(projects.core.infra)
    implementation(projects.core.api)
    implementation(projects.features.inventory.domain)
    implementation(projects.features.inventory.infra)
    implementation(projects.features.inventory.service)
    implementation(projects.features.inventory.api)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.security)
    implementation("com.graphql-java:graphql-java-extended-scalars:22.0")
    implementation(libs.kotlin.reflect)
    implementation(libs.liquibase.starter)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}
