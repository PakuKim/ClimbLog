package io.paku.climblog.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberVideoPicker(onVideoPicked: (Media.Video?) -> Unit): VideoPicker

interface VideoPicker {
    fun pickVideo()
}
