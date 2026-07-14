plugins {
    id("adventistportal.service")
}

group = "com.adventistportal.notification"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("notification-service")
}

dependencies {
    api(projects.features.notification.domain)
    api(projects.features.notification.infra)
}