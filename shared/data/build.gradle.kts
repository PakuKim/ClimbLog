plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":shared:domain"))
            implementation(libs.koin.core)
            implementation(libs.androidx.paging.common)
        }
    }
}
