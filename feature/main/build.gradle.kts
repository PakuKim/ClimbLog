plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:domain"))
            implementation(project(":shared:navigation"))
            implementation(project(":shared:ui-common"))
            implementation(project(":shared:platform"))
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewModel)
        }
    }
}
