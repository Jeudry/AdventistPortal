plugins {
    id("java-library")
    id("rosafiesta.kotlin-common")
}

group = "com.rosafiesta.core"
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
    implementation(projects.shared.api)
    
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}