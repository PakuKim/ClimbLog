package io.paku.climblog.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraManager(onResult: (PlatformMedia?) -> Unit): CameraManager

expect class CameraManager(
    onLaunch: () -> Unit
) {
    fun launch()
}