plugins {
    id("adventistportal.infra")
}

dependencies {
    implementation(projects.features.quotes.domain)
    implementation(projects.features.inventory.infra)
    implementation(projects.shared.domain)
    implementation(projects.core.infra)
    
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)
}