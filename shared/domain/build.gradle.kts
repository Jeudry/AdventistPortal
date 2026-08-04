plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(21)
}

base {
    archivesName.set("shared-domain")
}
