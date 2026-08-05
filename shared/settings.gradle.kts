// `shared` is its own Gradle build so it can be multiplatform: the backend build has no
// Android tooling, and the client build runs a different Kotlin version. Both include it.
rootProject.name = "shared"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include("domain")
include("service")
