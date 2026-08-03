plugins {
    id("adventistportal.infra")
    id("adventistportal.spring-boot-service")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

group = "com.adventistportal.inventory"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("inventory-infra")
}

dependencies {
    api(projects.features.inventory.domain)
    implementation(projects.shared.infra)

    implementation(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.security)
    runtimeOnly(libs.postgresql)
}