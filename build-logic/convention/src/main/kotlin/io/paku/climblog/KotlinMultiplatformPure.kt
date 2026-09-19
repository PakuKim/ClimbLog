package io.paku.climblog

import io.paku.climblog.ext.libs
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatformPure(
    extension: KotlinMultiplatformExtension
) {
    with(extension) {
        applyDefaultHierarchyTemplate()

        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = project.name.replaceFirstChar { it.uppercase() }
                isStatic = true
            }
        }
        jvm()
        js {
            browser()
        }

        sourceSets.getByName("commonMain").dependencies {
            implementation(libs.findLibrary("kotlinx.datetime").get())
            implementation(libs.findLibrary("kotlinx.coroutines.core").get())
        }
        sourceSets.getByName("commonTest").dependencies {
            implementation(libs.findLibrary("kotlin.test").get())
        }
    }
}
