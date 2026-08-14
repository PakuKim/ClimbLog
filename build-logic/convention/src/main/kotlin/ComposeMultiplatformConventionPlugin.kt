import io.paku.climblog.configureComposeMultiplatform
import io.paku.climblog.ext.applyPlugin
import io.paku.climblog.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPlugin(libs.findPlugin("composeMultiplatform").get())
                applyPlugin(libs.findPlugin("composeCompiler").get())
            }

            extensions.configure<KotlinMultiplatformExtension>(::configureComposeMultiplatform)
        }
    }
 }