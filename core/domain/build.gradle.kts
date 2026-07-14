plugins {
    id("java-library")
    kotlin("jvm")
}
group = "com.adventistportal.core"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("core-domain")
}

repositories {
    mavenCentral()
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