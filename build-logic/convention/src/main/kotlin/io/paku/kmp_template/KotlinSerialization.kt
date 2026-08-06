package io.paku.kmp_template

import io.paku.kmp_template.ext.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.text.get

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