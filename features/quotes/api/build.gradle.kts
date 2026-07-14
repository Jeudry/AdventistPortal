plugins {
    id("adventistportal.api")
}

dependencies {
    implementation(projects.features.quotes.service)
    implementation(projects.features.quotes.domain)
    implementation(projects.shared.domain)
    implementation(projects.core.api)
    
    implementation(libs.spring.boot.starter.graphql)
}