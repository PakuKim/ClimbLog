plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":shared:domain"))
            
            api(libs.compose.navigation)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
