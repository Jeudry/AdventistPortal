import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.jeudry.convention.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.compose.multiplatform.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.androidx.room.gradle.plugin)
    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.jeudry.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidComposeApplication") {
            id = "com.jeudry.convention.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("cmpApplication") {
            id = "com.jeudry.convention.cmp.application"
            implementationClass = "CmpApplicationConventionPlugin"
        }
        register("kmpLibrary") {
            id = "com.jeudry.convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("cmpLibrary") {
            id = "com.jeudry.convention.cmp.library"
            implementationClass = "CmpLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "com.jeudry.convention.cmp.feature"
            implementationClass = "CmpFeatureConventionPlugin"
        }
        register("buildKonfig") {
            id = "com.jeudry.convention.buildkonfig"
            implementationClass = "BuildKonfigConventionPlugin"
        }
        register("room") {
            id = "com.jeudry.convention.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}