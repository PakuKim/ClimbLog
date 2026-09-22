package io.paku.climblog.platform

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

actual class VideoPlayerController(
    private val _exoPlayer: ExoPlayer?
) {
    val exoPlayer: ExoPlayer
        get() = _exoPlayer ?: throw IllegalStateException("ExoPlayer is not available in preview mode")

    actual fun play() {
        _exoPlayer?.play()
    }

    actual fun pause() {
        _exoPlayer?.pause()
    }

    actual fun setPlaybackSpeed(speed: Float) {
        _exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    actual fun seekTo(positionMs: Long) {
        _exoPlayer?.seekTo(positionMs)
    }

    actual fun release() {
        _exoPlayer?.release()
    }
}

@Composable
actual fun rememberVideoPlayerController(url: String): VideoPlayerController {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        return remember { VideoPlayerController(null) }
    }

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    return remember(exoPlayer) {
        VideoPlayerController(exoPlayer)
    }
}

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayerView(
    controller: VideoPlayerController,
    modifier: Modifier
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(Color.Black))
        return
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = controller.exoPlayer
                useController = false
                // RESIZE_MODE_ZOOM = 4
                resizeMode = 4 
            }
        },
        modifier = modifier,
        update = { playerView ->
            playerView.player = controller.exoPlayer
        }
    )
}
