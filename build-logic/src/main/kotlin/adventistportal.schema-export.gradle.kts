import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Gives a service the offline model-schema exporter.
 *
 * It runs with the service's own classpath, so each service exports the entities it
 * actually owns and diffs them against its own changelog. The source lives once, under
 * gradle/tooling, rather than copied into every service.
 */
val toolingSources = rootProject.layout.projectDirectory.dir("gradle/tooling/kotlin")

extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>("kotlin") {
    sourceSets.named("test") { kotlin.srcDir(toolingSources) }
}

tasks.register<Test>("exportModelSchema") {
    group = "migrations"
    description = "Export this service's JPA model to build/model-schema.sql (offline, no database)."
    testClassesDirs = project.the<SourceSetContainer>()["test"].output.classesDirs
    classpath = project.the<SourceSetContainer>()["test"].runtimeClasspath
    useJUnitPlatform()
    filter { includeTestsMatching("com.adventistportal.tooling.ModelSchemaExportTest") }
    systemProperty(
        "modelSchemaOut",
        layout.buildDirectory.file("model-schema.sql").get().asFile.absolutePath,
    )
    outputs.upToDateWhen { false }
}

// The exporter must not run as part of `check`: it is a tool, not an assertion. A
// service with no tests of its own is then left with an empty filter, which Gradle
// treats as an error unless told otherwise.
tasks.named<Test>("test") {
    filter {
        excludeTestsMatching("com.adventistportal.tooling.ModelSchemaExportTest")
        isFailOnNoMatchingTests = false
    }
}
