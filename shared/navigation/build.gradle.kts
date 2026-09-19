plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(project(":shared:domain"))
            
            api(libs.compose.navigation)
            api(libs.compose.material3)
            api(libs.material.icons.extended)
            api(libs.kotlinx.serialization.json)
        }
    }
}
