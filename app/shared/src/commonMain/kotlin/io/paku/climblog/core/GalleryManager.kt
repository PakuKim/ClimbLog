package io.paku.climblog.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryManager(onResult: (Media?) -> Unit): GalleryManager

expect class GalleryManager(
    onLaunch: () -> Unit
) {
    fun launch()
}