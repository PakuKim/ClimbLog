package io.paku.kmp_template

import io.paku.kmp_template.ext.libs
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureComposeMultiplatform(
    extension: KotlinMultiplatformExtension
) {
    extension.apply {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("compose.runtime").get())
                implementation(libs.findLibrary("compose.foundation").get())
                implementation(libs.findLibrary("compose.material3").get())
                implementation(libs.findLibrary("compose.ui").get())
                implementation(libs.findLibrary("compose.uiToolingPreview").get())
                implementation(libs.findLibrary("compose.components.resources").get())
                implementation(libs.findLibrary("compose.animation").get())
            }
        }
    }
}