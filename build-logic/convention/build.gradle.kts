import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.plugin.use.PluginDependency

plugins {
    `kotlin-dsl`
}

group = "io.paku.kmp_template.buildlogic"

dependencies {
    compileOnly(libs.plugins.androidApplication.toDep())
    compileOnly(libs.plugins.androidMultiplatformLibrary.toDep())
    compileOnly(libs.plugins.kotlinMultiplatform.toDep())
    compileOnly(libs.plugins.composeMultiplatform.toDep())
    compileOnly(libs.plugins.composeCompiler.toDep())
    compileOnly(libs.plugins.kotlinSerialization.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "io.paku.kmp_template.androidApp"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "io.paku.kmp_template.composeMultiplatform"
            implementationClass = "ComposeMultiplatformConventionPlugin"
        }
        register("kotlinMultiplatform") {
            id = "io.paku.kmp_template.kotlinMultiplatform"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "io.paku.kmp_template.kotlinSerialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}