plugins {
    alias(libs.plugins.kmp.kotlinMultiplatformFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:domain"))
            implementation(project(":shared:navigation"))
            implementation(project(":shared:ui-common"))
            implementation(project(":shared:platform"))
        }
    }
}
