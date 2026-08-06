import io.paku.kmp_template.configureComposeMultiplatform
import io.paku.kmp_template.ext.applyPlugin
import io.paku.kmp_template.ext.libs
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