import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("adventistportal.spring-boot-app")
    id("adventistportal.schema-export")
}
group = "com.adventistportal.notification"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal notification service"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

springBoot {
    mainClass.set("com.adventistportal.notification.NotificationServiceApplicationKt")
}

tasks {
    named<BootJar>("bootJar") {
        // The templates the mail service renders live with the feature, not here.
        from(project(":features:notification:infra").projectDir.resolve("src/main/resources")) {
            into("")
        }
    }
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(projects.core.domain)
    implementation(projects.core.infra)
    implementation(projects.core.api)
    implementation(projects.features.notification.domain)
    implementation(projects.features.notification.infra)
    implementation(projects.features.notification.service)
    implementation(projects.features.notification.api)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.kotlin.reflect)
    implementation(libs.liquibase.starter)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.awaitility)
    testImplementation(libs.greenmail.junit5)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}
