package io.paku.climblog

import io.paku.climblog.ext.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.configureKotlinSerialization(
    extension: KotlinMultiplatformExtension
) {
    with(extension) {
        sourceSets {
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}