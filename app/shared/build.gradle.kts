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
        commonMain.dependencies {
            api(project(":shared:domain"))
            api(project(":shared:data"))
            api(project(":shared:local"))
            api(project(":shared:remote"))
            api(project(":shared:navigation"))
            api(project(":shared:ui-common"))
            api(project(":shared:platform"))
            api(project(":feature:main"))
            api(project(":feature:onboard"))

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewModel)
            implementation(libs.koin.compose.viewModel.navigation)

            implementation(libs.gitliveFirebaseMessaging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.testing)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}