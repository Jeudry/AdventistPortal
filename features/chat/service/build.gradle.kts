plugins {
    id("adventistportal.service")
    id("adventistportal.spring-boot-service")
    kotlin("plugin.spring")
}

group = "com.adventistportal.chat"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("chat-service")
}

dependencies {
    implementation(projects.features.chat.domain)
    implementation(projects.features.chat.infra)
    
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.amqp)
}