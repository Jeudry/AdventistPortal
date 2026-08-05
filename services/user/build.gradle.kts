plugins {
    id("adventistportal.spring-boot-app")
}
group = "com.adventistportal.user"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal user service"

springBoot {
    mainClass.set("com.adventistportal.user.UserServiceApplicationKt")
}

dependencies {
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
