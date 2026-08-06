import io.paku.kmp_template.configureKotlinMultiplatform
import io.paku.kmp_template.ext.applyPlugin
import io.paku.kmp_template.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPlugin(libs.findPlugin("kotlinMultiplatform").get())
                applyPlugin(libs.findPlugin("androidMultiplatformLibrary").get())
            }

            extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
        }
    }
}