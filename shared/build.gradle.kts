// The plugin versions live here so both modules share one classloader; declaring them
// per-module makes Gradle load the Kotlin plugin twice and the native tasks clash.
plugins {
    kotlin("multiplatform") version "2.3.21" apply false
    kotlin("plugin.serialization") version "2.3.21" apply false
}

subprojects {
    group = "com.adventistportal.shared"
    version = "1.0.0"
}
