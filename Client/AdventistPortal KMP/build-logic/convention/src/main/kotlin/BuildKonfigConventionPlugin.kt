import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.adventistportal.convention.pathToPackageName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }

            extensions.configure<BuildKonfigExtension> {
                packageName = target.pathToPackageName()
                defaultConfigs {
                    val localProperties = gradleLocalProperties(rootDir, rootProject.providers)

                    val apiKey = localProperties.getProperty("API_KEY")
                        ?: throw IllegalStateException(
                            "Missing API_KEY property in local.properties"
                        )
                    buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)

                    // Where the gateway is. Production by default, so an existing checkout
                    // keeps working; point BASE_URL at a local one to run against the
                    // services on your machine. Everything the client reaches goes through
                    // it — there is no second address to keep in step.
                    val baseUrl = localProperties.getProperty("BASE_URL")
                        ?: "https://adventistportal.com"
                    buildConfigField(FieldSpec.Type.STRING, "BASE_URL", baseUrl)
                }
            }
        }
    }
}