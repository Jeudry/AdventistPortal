plugins {
    id("rosafiesta.api")
}
repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}
group = "com.rosafiesta.shared"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("shared-api")
}

dependencies {
    api(projects.shared.domain)
    api(projects.shared.service)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}