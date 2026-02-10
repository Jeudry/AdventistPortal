rootProject.name = "build-logic"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            // Use a robust path to the root project's gradle/libs.versions.toml so this included build
            // can always locate the catalog regardless of current working directory.
            from(files(rootDir.parentFile.resolve("gradle/libs.versions.toml")))
        }
    }
}