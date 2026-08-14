import io.paku.climblog.configureKotlinSerialization
import io.paku.climblog.ext.applyPlugin
import io.paku.climblog.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinSerializationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPlugin(libs.findPlugin("kotlinSerialization").get())
            }

            extensions.configure<KotlinMultiplatformExtension>(::configureKotlinSerialization)
        }
    }
}