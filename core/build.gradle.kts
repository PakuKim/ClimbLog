plugins {
    id("io.paku.climblog.kotlinMultiplatformPure")
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.koin.core)
        }
    }
}
