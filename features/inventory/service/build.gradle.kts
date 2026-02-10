plugins {
    id("rosafiesta.service")
    id("rosafiesta.spring-boot-service")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

group = "com.rosafiesta.inventory"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("inventory-service")
}

dependencies {
    api(projects.features.inventory.domain)
    api(projects.features.inventory.infra)
    implementation(projects.core.service)
    implementation(projects.shared.service)

    implementation(libs.spring.boot.starter.security)
    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)
}