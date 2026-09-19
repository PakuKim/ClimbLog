plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(project(":shared:data"))
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.serialization)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.ktor.client.darwin)
        }
    }
}
