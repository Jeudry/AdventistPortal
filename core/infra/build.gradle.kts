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
    api(projects.contracts)
    implementation(projects.core.domain)
    
    implementation(libs.spring.boot.starter.amqp)
    // The outbox writes through JDBC so it can share the caller's transaction without
    // pinning core to one schema through an entity.
    implementation(libs.spring.boot.starter.jdbc)
    
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