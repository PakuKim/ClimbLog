plugins {
    alias(libs.plugins.kmp.kotlinMultiplatformPure)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            api(libs.logger)
        }
    }
}
