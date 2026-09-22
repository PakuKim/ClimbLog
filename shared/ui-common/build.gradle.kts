plugins {
    alias(libs.plugins.kmp.kotlinMultiplatform)
    alias(libs.plugins.kmp.composeMultiplatform)
}

compose {
    resources {
        packageOfResClass = "io.paku.climblog.shared.ui_common"
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
        }

        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":shared:domain"))
            implementation(project(":shared:navigation"))
            implementation(project(":shared:platform"))
            
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.animation)
            implementation(libs.compose.navigation)
            
            api(libs.coil3)
            api(libs.coil3.compose)
            api(libs.material.icons.extended)
            
            api(libs.androidx.lifecycle.viewmodelCompose)
            api(libs.androidx.lifecycle.runtimeCompose)
            api(libs.androidx.paging.compose)
            implementation(libs.androidx.paging.common)
        }
    }
}
