plugins {
    id("adventistportal.domain")
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.shared.domain)
}