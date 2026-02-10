plugins {
    id("rosafiesta.api")
    id("rosafiesta.spring-boot-service")
    kotlin("plugin.spring")
}

group = "com.rosafiesta.chat"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("chat-api")
}

dependencies {
    implementation(projects.core.service)
    implementation(projects.core.api)
    
    implementation(projects.features.chat.domain)
    implementation(projects.features.chat.infra)
    implementation(projects.features.chat.service)
    
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.jackson.datatype.jsr310)
}