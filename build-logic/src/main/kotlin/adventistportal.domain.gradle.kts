plugins {
    id("java-library")
    kotlin("jvm")
    // Domain models cross process boundaries — cached in Redis, shared with the client.
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    api(libraries.findLibrary("kotlinx-serialization-json").get())
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}