plugins {
    id("adventistportal.api")
}
group = "com.adventistportal.user"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("user-api")
}

dependencies {
    api(projects.features.user.domain)
    api(projects.features.user.service)
    implementation(projects.core.service)
    implementation(projects.core.api)
    implementation(projects.shared.api)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.jwt.api)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
}