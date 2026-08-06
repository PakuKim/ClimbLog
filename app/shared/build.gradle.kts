plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
    alias(libs.plugins.kmp.composeMultiplatform)
}

kotlin {
    android {
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.koin.android)
            implementation(libs.ktor.okhttp)
//            implementation(libs.androidx.datastore.preferences.core)
            api(libs.coil3.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.ios)
            implementation(libs.ktor.darwin)
        }
        commonMain.dependencies {
            api(project(":core"))
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization)

//            api(libs.coil3.core)
//            api(libs.coil3.network)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}