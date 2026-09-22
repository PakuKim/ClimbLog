package io.paku.climblog.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryManager(onResult: (Media?) -> Unit): GalleryManager

expect class GalleryManager(
    onLaunch: () -> Unit
) {
    fun launch()
}