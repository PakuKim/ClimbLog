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
            api(project(":core"))
            api(project(":shared:domain"))
            api(project(":shared:navigation"))
            api(project(":shared:platform"))
            
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.animation)
            implementation(libs.compose.navigation)
            
            implementation(libs.coil3)
            implementation(libs.coil3.compose)
            implementation(libs.material.icons.extended)
            
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.paging.common)
        }
    }
}
