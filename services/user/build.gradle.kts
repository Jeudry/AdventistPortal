plugins {
    id("adventistportal.spring-boot-app")
    id("adventistportal.schema-export")
}
group = "com.adventistportal.user"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal user service"

springBoot {
    mainClass.set("com.adventistportal.user.UserServiceApplicationKt")
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(projects.core.domain)
    implementation(projects.core.infra)
    implementation(projects.core.service)
    implementation(projects.core.api)
    implementation(projects.features.user.domain)
    implementation(projects.features.user.infra)
    implementation(projects.features.user.service)
    implementation(projects.features.user.api)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.grpc.netty.shaded)
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
