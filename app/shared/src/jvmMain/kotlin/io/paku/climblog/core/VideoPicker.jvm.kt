package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberVideoPicker(onVideoPicked: (VideoFile?) -> Unit): VideoPicker {
    return remember {
        object : VideoPicker {
            override fun pickVideo() {
                onVideoPicked(null)
            }
        }
    }
}
