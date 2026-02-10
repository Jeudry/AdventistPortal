plugins {
    id("rosafiesta.service")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

group = "com.rosafiesta.shared"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("shared-service")
}

dependencies {
    api(projects.shared.domain)
    api(projects.shared.infra)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}