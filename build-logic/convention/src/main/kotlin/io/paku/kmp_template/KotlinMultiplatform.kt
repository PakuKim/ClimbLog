package io.paku.kmp_template

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.paku.kmp_template.ext.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.jvm.kotlin

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
                baseName = target.name
                isStatic = true
            }
        }
        jvm()
        js {
            browser()
        }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser()
        }

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
        namespace = "io.paku.kmp_template.${target.name.replace("-", ".")}"
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