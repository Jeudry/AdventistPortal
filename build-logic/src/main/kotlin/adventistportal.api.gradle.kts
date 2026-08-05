plugins {
    id("adventistportal.kotlin-common")
    id("java-library")
    // The DTOs are @Serializable so they can move to `shared` and be used by the KMP
    // client. Without this plugin the annotation generates nothing, no serialiser is
    // found, and Spring quietly falls back to Jackson.
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(project(":core:domain"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}