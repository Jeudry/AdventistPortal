plugins {
    id("adventistportal.api")
}
group = "com.adventistportal.inventory"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("inventory-api")
}
dependencies {
    api(projects.features.inventory.domain)
    api(projects.features.inventory.service)
    implementation(projects.features.inventory.infra)
    implementation("com.adventistportal.shared:service:1.0.0")
    implementation(projects.core.service)
    implementation(projects.core.api)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.jwt.api)
}