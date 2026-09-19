plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain"))
            api(project(":shared:navigation"))
            api(project(":shared:ui-common"))
            api(project(":shared:platform"))
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewModel)
            
            implementation(libs.compose.navigation)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.coil3)
            implementation(libs.coil3.compose)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
    }
}
