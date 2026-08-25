package io.paku.climblog.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.UIKit.UIView

actual class VideoPlayerController(
    val player: AVPlayer,
    val playerLayer: AVPlayerLayer
) {
    actual fun play() {
        player.play()
    }

    actual fun pause() {
        player.pause()
    }

    actual fun setPlaybackSpeed(speed: Float) {
        player.rate = speed
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun seekTo(positionMs: Long) {
        val time = CMTimeMake(positionMs, 1000)
        player.seekToTime(time)
    }

    actual fun release() {
        player.pause()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberVideoPlayerController(url: String): VideoPlayerController {
    val player = remember(url) {
        val nsUrl = NSURL.URLWithString(url)!!
        AVPlayer.playerWithURL(nsUrl)
    }
    
    val playerLayer = remember(player) {
        AVPlayerLayer.playerLayerWithPlayer(player)
    }

    DisposableEffect(player) {
        val notificationCenter = NSNotificationCenter.defaultCenter
        val observer = notificationCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            player.seekToTime(CMTimeMake(0, 1))
            player.play()
        }
        
        onDispose {
            player.pause()
            notificationCenter.removeObserver(observer)
        }
    }

    return remember(player, playerLayer) {
        VideoPlayerController(player, playerLayer)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayerView(
    controller: VideoPlayerController,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val view = UIView()
            view.layer.addSublayer(controller.playerLayer)
            view
        },
        modifier = modifier,
        update = { view ->
            CATransaction.begin()
            CATransaction.setDisableActions(true)
            controller.playerLayer.frame = view.bounds
            CATransaction.commit()
        }
    )
}
