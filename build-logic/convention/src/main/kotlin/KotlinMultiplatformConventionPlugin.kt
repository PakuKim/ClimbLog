import io.paku.climblog.configureKotlinMultiplatform
import io.paku.climblog.ext.applyPlugin
import io.paku.climblog.ext.libs
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