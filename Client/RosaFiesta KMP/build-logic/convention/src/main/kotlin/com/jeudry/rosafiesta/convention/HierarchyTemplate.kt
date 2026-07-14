@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

package com.adventistportal.convention

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

// AGP 9's com.android.kotlin.multiplatform.library registers an Android target
// that withAndroidTarget() (which matches the legacy androidTarget()) does not
// recognize. Match by platform type so intermediate source sets (jvmCommon,
// mobile) still include the Android target.
private val KotlinPlatformType?.isAndroid
    get() = this == KotlinPlatformType.androidJvm

private val hierarchyTemplate = KotlinHierarchyTemplate {
    withSourceSetTree(
        KotlinSourceSetTree.main,
        KotlinSourceSetTree.test,
    )

    common {
        withCompilations { true }

        group("mobile") {
            withCompilations { it.target.platformType.isAndroid }
            group("ios") {
                withIos()
            }
        }

        group("jvmCommon") {
            withCompilations { it.target.platformType.isAndroid }
            withJvm()
        }

        group("native") {
            withNative()

            group("apple") {
                withApple()

                group("ios") {
                    withIos()
                }

                group("macos") {
                    withMacos()
                }
            }
        }
    }
}

fun KotlinMultiplatformExtension.applyHierarchyTemplate() {
    applyHierarchyTemplate(hierarchyTemplate)
}