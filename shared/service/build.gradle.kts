plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }
    }

    jvmToolchain(21)
}

base {
    archivesName.set("shared-service")
}
