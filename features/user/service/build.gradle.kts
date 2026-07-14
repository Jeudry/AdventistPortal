plugins {
    id("adventistportal.service")
    id("adventistportal.spring-boot-service")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

group = "com.adventistportal.user"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("user-service")
}

dependencies {
    api(projects.features.user.domain)
    api(projects.features.user.infra)
    implementation(projects.core.service)

    implementation(libs.spring.boot.starter.security)
    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)
}