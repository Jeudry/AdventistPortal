plugins {
    id("rosafiesta.domain")
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.shared.domain)
}