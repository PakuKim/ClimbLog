import com.android.build.api.dsl.ApplicationExtension
import io.paku.kmp_template.ext.applyPlugin
import io.paku.kmp_template.ext.debugImplementations
import io.paku.kmp_template.ext.implementations
import io.paku.kmp_template.ext.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            applyPlugin(libs.findPlugin("androidApplication").get())
            applyPlugin(libs.findPlugin("composeCompiler").get())
            applyPlugin(libs.findPlugin("kotlinParcelize").get())
        }

        dependencies {
            implementations(
                libs.findLibrary("androidx.activity.compose").get(),
                libs.findLibrary("compose.uiToolingPreview").get()
            )

            debugImplementations(
                libs.findLibrary("compose.uiTooling").get()
            )
        }

        extensions.configure<KotlinAndroidProjectExtension> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        extensions.configure<ApplicationExtension> {
            namespace = "io.paku.kmp_template"
            compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()

            defaultConfig {
                applicationId = "io.paku.kmp_template"
                minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                targetSdk = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}