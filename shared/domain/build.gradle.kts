plugins {
    id("adventistportal.domain")
}

repositories {
    mavenCentral()
}

group = "com.adventistportal.shared"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("shared-domain")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}