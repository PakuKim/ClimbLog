package io.paku.climblog.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.paku.climblog.core.VideoPlayerController

actual class VideoPlayerController {
    actual fun play() {}
    actual fun pause() {}
    actual fun setPlaybackSpeed(speed: Float) {}
    actual fun seekTo(positionMs: Long) {}
    actual fun release() {}
}

@Composable
actual fun rememberVideoPlayerController(url: String): VideoPlayerController =
    io.paku.climblog.core.VideoPlayerController()

@Composable
actual fun VideoPlayerView(
    controller: VideoPlayerController,
    modifier: Modifier
) {}
