import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.annotation


plugins {
    id("adventistportal.spring-boot-service")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // Every service reports the same way: one request gets one trace id, and that id is
    // on every log line it causes, in whichever process.
    "implementation"(libraries.findLibrary("spring-boot-starter-actuator").get())
    "implementation"(libraries.findLibrary("spring-boot-micrometer-tracing").get())
    "implementation"(libraries.findLibrary("spring-boot-micrometer-tracing-brave").get())
    "implementation"(libraries.findLibrary("spring-boot-zipkin").get())
    "implementation"(libraries.findLibrary("micrometer-tracing-bridge-brave").get())
    "implementation"(libraries.findLibrary("zipkin-reporter-brave").get())
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}