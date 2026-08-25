package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class VideoPlayerController {
    fun play()
    fun pause()
    fun setPlaybackSpeed(speed: Float)
    fun seekTo(positionMs: Long)
    fun release()
}

@Composable
expect fun rememberVideoPlayerController(url: String): VideoPlayerController

@Composable
expect fun VideoPlayerView(
    controller: VideoPlayerController,
    modifier: Modifier
)
