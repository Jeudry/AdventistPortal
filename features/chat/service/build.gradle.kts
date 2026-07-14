plugins {
    id("rosafiesta.service")
    id("rosafiesta.spring-boot-service")
    kotlin("plugin.spring")
}

group = "com.rosafiesta.chat"
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