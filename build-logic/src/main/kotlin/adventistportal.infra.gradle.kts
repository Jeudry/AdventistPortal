plugins {
    id("adventistportal.kotlin-common")
    id("java-library")
    // JPA instantiates entities reflectively, so it needs a no-arg constructor Kotlin
    // does not generate. Without this an entity only works while every field happens
    // to have a default, and fails at runtime the moment one does not.
    kotlin("plugin.jpa")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:infra"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}