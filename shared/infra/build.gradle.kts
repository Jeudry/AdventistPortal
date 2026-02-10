plugins {
    id("rosafiesta.infra")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

group = "com.rosafiesta.shared"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("shared-infra")
}

dependencies {
    api(projects.shared.domain)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}