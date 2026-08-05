plugins {
    id("java-library")
    id("adventistportal.kotlin-common")
}

group = "com.adventistportal.core"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("core-api")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(projects.core.domain)
    implementation("com.adventistportal.shared:domain:1.0.0")
    
    implementation(libs.spring.boot.starter.web)
    api(libs.kotlinx.serialization.json)
    implementation(libs.spring.boot.starter.security)
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}