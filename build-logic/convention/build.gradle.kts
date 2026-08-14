plugins {
    `kotlin-dsl`
}

group = "io.paku.climblog.buildlogic"

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
            id = "io.paku.climblog.androidApp"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "io.paku.climblog.composeMultiplatform"
            implementationClass = "ComposeMultiplatformConventionPlugin"
        }
        register("kotlinMultiplatform") {
            id = "io.paku.climblog.kotlinMultiplatform"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "io.paku.climblog.kotlinSerialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}