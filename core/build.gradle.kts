plugins {
    alias(libs.plugins.kmp.kotlinMultiplatformPure)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.logger)
        }
    }
}
