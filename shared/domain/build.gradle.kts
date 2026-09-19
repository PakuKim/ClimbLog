plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            implementation(libs.koin.core)
            implementation(libs.androidx.paging.common)
        }
    }
}
