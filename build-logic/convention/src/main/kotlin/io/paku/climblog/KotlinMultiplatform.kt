package io.paku.climblog

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.paku.climblog.ext.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform(
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
                binaryOption("bundleId", "io.paku.climblog.${project.name.lowercase()}")
            }
        }
        jvm()
        js {
            browser()
        }
//        @OptIn(ExperimentalWasmDsl::class)
//        wasmJs {
//            browser()
//        }

        configure<KotlinMultiplatformAndroidLibraryTarget>(::configureKotlinMultiplatformLibrary)


        sourceSets {
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx.datetime").get())
                implementation(libs.findLibrary("kotlinx.coroutines.core").get())
            }
            commonTest.dependencies {
                implementation(libs.findLibrary("kotlin.test").get())
            }
        }
    }
}

internal fun Project.configureKotlinMultiplatformLibrary(
    target: KotlinMultiplatformAndroidLibraryTarget
) {
    with(target) {
        val modulePath = project.path.substring(1)
            .replace(":", ".")
            .replace("-", "_")

        namespace = "io.paku.climblog.$modulePath"
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }
}