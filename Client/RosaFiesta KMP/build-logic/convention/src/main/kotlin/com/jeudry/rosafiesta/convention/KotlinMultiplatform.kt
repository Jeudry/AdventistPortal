package com.jeudry.rosafiesta.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform() {
    extensions.configure<KotlinMultiplatformExtension> {
        // Android target via com.android.kotlin.multiplatform.library (AGP 9).
        // Replaces com.android.library's LibraryExtension + androidTarget().
        (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension>("androidLibrary") {
            namespace = this@configureKotlinMultiplatform.pathToPackageName()
            compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
            minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
        }

        configureDesktopTarget()

        // iOS targets: iosArm64 (device) + iosSimulatorArm64 (Apple-silicon
        // simulator). iosX64 (Intel-Mac simulator) is dropped because Coil 3.5
        // and Compose Multiplatform 1.11.1 no longer publish iosX64 artifacts.
        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = this@configureKotlinMultiplatform.pathToFrameworkName()
            }
        }

        applyHierarchyTemplate()

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }
}
