package io.paku.kmp_template

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.paku.kmp_template.ext.libs
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal fun Project.configureKotlinMultiplatform(
    target: KotlinMultiplatformAndroidLibraryTarget
) {
    target.apply {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        namespace = "io.paku.kmp_template.${target.name.replace("-", ".")}"
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
    }
}