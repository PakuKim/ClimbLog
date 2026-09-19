import com.codingfeline.buildkonfig.compiler.FieldSpec
import io.paku.climblog.ext.Configs

plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
    alias(libs.plugins.kmp.composeMultiplatform)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

buildkonfig {
    packageName = "io.paku.climblog"
    defaultConfigs {
        Configs.DEV.toBuildKonfig(project).forEach { (key, value) ->
            buildConfigField(FieldSpec.Type.STRING, key, value)
        }
    }
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
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.exoplayer.hls)
            implementation(libs.androidx.media3.transformer)
            implementation(libs.androidx.media3.effect)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.media3.session)
            implementation(libs.firebaseMessaging)
            implementation(libs.spectrum)
            implementation(libs.accompanist.permissions)
            implementation(libs.androidx.sqlite.bundled)
            api(libs.googleCredentials)
            api(libs.googleCredentialsPlay)
            api(libs.googleId)
            api(libs.kakaoLogin)
            api(libs.naverLogin)
            api(libs.coil3.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.ktor.client.darwin)
            implementation(libs.androidx.sqlite.bundled)
        }
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

            implementation(libs.logger)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.compose.navigation)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewModel)
            implementation(libs.koin.compose.viewModel.navigation)

            implementation(libs.coil3)
            implementation(libs.coil3.compose)
            implementation(libs.material.icons.extended)

            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.gitliveFirebaseMessaging)
            implementation(libs.androidx.room.runtime)
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

room3 {
    schemaDirectory("$projectDir/schemas")
}


dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
