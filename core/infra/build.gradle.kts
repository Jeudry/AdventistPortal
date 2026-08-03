plugins {
    id("java-library")
    id("adventistportal.kotlin-common")
}

group = "com.adventistportal.core"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("core-infra")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(projects.core.domain)
    
    implementation(libs.spring.boot.starter.amqp)
    
    api(libs.jackson.module.kotlin)
    api(libs.kotlin.reflect)
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}