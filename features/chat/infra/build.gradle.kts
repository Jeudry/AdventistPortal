plugins {
    id("adventistportal.infra")
    id("adventistportal.spring-boot-service")
    kotlin("plugin.spring")
}

group = "com.adventistportal.chat"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("chat-infra")
}

dependencies {
    implementation(projects.core.api)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(projects.features.chat.domain)
    
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.websocket)
    
    runtimeOnly(libs.postgresql)
}