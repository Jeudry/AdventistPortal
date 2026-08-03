import com.adventistportal.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

class CmpLibraryConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.adventistportal.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            // Use the Compose Multiplatform DSL dependencies so the plugin
            // resolves the correct per-platform artifacts (raw
            // org.jetbrains.compose.* coordinates don't resolve for all targets).
            val compose = extensions.getByType<ComposeExtension>().dependencies

            dependencies {
                "commonMainImplementation"(compose.ui)
                "commonMainImplementation"(compose.foundation)
                "commonMainImplementation"(compose.material3)
                "commonMainImplementation"(compose.materialIconsExtended)

                "androidRuntimeClasspath"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}
