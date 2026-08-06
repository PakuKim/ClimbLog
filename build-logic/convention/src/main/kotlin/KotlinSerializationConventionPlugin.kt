import io.paku.kmp_template.configureKotlinSerialization
import io.paku.kmp_template.ext.applyPlugin
import io.paku.kmp_template.ext.libs
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