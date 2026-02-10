plugins {
    id("rosafiesta.api")
}

group = "com.rosafiesta.notification"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("notification-api")
}

dependencies {
    api(projects.features.notification.domain)
    api(projects.features.notification.service)
    api(projects.core.api)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
}