import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import io.paku.kmp_template.configureKotlinMultiplatform
import io.paku.kmp_template.ext.applyPlugin
import io.paku.kmp_template.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPlugin(libs.findPlugin("kotlinMultiplatform").get())
                applyPlugin(libs.findPlugin("androidMultiplatformLibrary").get())
            }

            extensions.configure<KotlinMultiplatformExtension> {
                this.configure<KotlinMultiplatformAndroidLibraryTarget>(::configureKotlinMultiplatform)

                sourceSets {
                    commonMain.dependencies {
                        implementation(libs.findLibrary("kotlinx.datetime").get())
                        implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                        implementation(libs.findLibrary("koin.core").get())
                    }
                }
            }
        }
    }
}