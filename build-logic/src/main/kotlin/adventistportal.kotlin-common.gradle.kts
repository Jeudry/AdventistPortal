import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libraries.findVersion("spring-boot").get()}")
    }
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Ensure generated jars have a unique name across the monorepo using the project path
// Example: :user:api -> adventistportal-user-api
// Apply only to projects that actually produce JARs or BootJars
val baseFromPath: () -> String = {
    project.path.removePrefix(":").replace(":", "-").ifEmpty { project.name }
}

// Make the artifact prefix and plain-jar suffix configurable via -PartifactPrefix / -PplainJarSuffix or gradle.properties
// Default artifactPrefix is empty so generated names are based on project.path (e.g. :user:api -> user-api)
// Keep plainJarSuffix configured (used when plain jars are produced). We will disable plain `jar` for Spring Boot apps
// by default (they usually don't need a plain library jar), but we leave the suffix available if you re-enable it later.
val artifactPrefix: String = (project.findProperty("artifactPrefix") as? String).takeIf { it?.isNotBlank() == true } ?: ""
val plainJarSuffix: String = (project.findProperty("plainJarSuffix") as? String).takeIf { it?.isNotBlank() == true } ?: "-plain"

// Apply to regular jars for projects that have java/java-library/kotlin-jvm plugins
listOf("java", "java-library", "org.jetbrains.kotlin.jvm").forEach { pluginId ->
    plugins.withId(pluginId) {
        // default: set the normal base name for plain jars (for libraries)
        tasks.withType<Jar> {
            archiveBaseName.set(artifactPrefix + baseFromPath())
        }
    }
}

// If Spring Boot is present, override the plain `jar` basename to include the plain suffix but disable it by default
plugins.withId("org.springframework.boot") {
    // configure plain jar name so the setting is ready if enabled later
    tasks.named<Jar>("jar") {
        // set the plain jar name pattern (kept for documentation/possible re-enable)
        archiveBaseName.set(artifactPrefix + baseFromPath() + plainJarSuffix)
        // Disable producing the plain jar for Spring Boot apps by default — typical apps only need the bootJar.
        // To re-enable plain jar production in the future, remove or comment out this `enabled = false` line
        // and (optionally) keep the naming above so the plain jar uses the expected suffix.
        enabled = false
    }

    // configure the executable bootJar
    tasks.withType<BootJar> {
        archiveBaseName.set(artifactPrefix + baseFromPath())
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}