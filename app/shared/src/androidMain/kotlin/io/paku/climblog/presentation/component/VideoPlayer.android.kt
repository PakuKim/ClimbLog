package io.paku.climblog.presentation.component

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

actual class VideoPlayerController(
    val exoPlayer: ExoPlayer
) {
    actual fun play() {
        exoPlayer.play()
    }

    actual fun pause() {
        exoPlayer.pause()
    }

    actual fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
    }

    actual fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    actual fun release() {
        exoPlayer.release()
    }
}

@Composable
actual fun rememberVideoPlayerController(url: String): VideoPlayerController {
    val context = LocalContext.current
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
