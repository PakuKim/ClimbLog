plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(project(":shared:domain"))
            implementation(libs.koin.core)
            
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.exoplayer.hls)
            implementation(libs.androidx.media3.transformer)
            implementation(libs.androidx.media3.effect)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.media3.session)
            implementation(libs.androidx.activity.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.spectrum)
            api(libs.googleCredentials)
            api(libs.googleCredentialsPlay)
            api(libs.googleId)
            api(libs.kakaoLogin)
            api(libs.naverLogin)
        }
        iosMain.dependencies {
            // iOS specific native dependencies
        }
    }
}
