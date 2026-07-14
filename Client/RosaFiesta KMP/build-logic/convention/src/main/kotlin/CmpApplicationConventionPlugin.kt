import com.jeudry.rosafiesta.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * AGP 9: a KMP module can no longer be a `com.android.application` and use
 * `androidTarget()` at the same time. The shared UI (composeApp) is therefore a
 * Compose Multiplatform *library* (android via com.android.kotlin.multiplatform.library,
 * plus desktop and iOS); the Android APK lives in the separate `:androidApp` module,
 * which applies `com.android.application` and depends on this library.
 */
class CmpApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.jeudry.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            dependencies {
                // KMP Android library plugin has no build variants, so use the
                // android runtime classpath instead of debugImplementation.
                "androidRuntimeClasspath"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}
