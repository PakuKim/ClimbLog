package io.paku.climblog.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.paku.climblog.domain.model.MediaSource
import io.paku.climblog.domain.model.permission.PermissionType

actual class VideoPlayerController {
    actual fun play() {}
    actual fun pause() {}
    actual fun setPlaybackSpeed(speed: Float) {}
    actual fun seekTo(positionMs: Long) {}
    actual fun release() {}
}

@Composable
actual fun rememberVideoPlayerController(url: String): VideoPlayerController = VideoPlayerController()

@Composable
actual fun VideoPlayerView(controller: VideoPlayerController, modifier: Modifier) {}

actual class PlatformMedia : MediaSource {
    actual override suspend fun readBytes(): ByteArray = ByteArray(0)
    actual override suspend fun readChunk(start: Long, length: Int): ByteArray = ByteArray(0)
    actual override suspend fun getSize(): Long = 0
}

actual fun shareLink(url: String) {}

actual class CameraManager actual constructor(onLaunch: () -> Unit) {
    actual fun launch() {}
}

@Composable
actual fun rememberCameraManager(onResult: (PlatformMedia?) -> Unit): CameraManager = CameraManager {}

actual class GalleryManager actual constructor(onLaunch: () -> Unit) {
    actual fun launch() {}
}

@Composable
actual fun rememberGalleryManager(onResult: (Media?) -> Unit): GalleryManager = GalleryManager {}

class JvmImagePicker : ImagePicker {
    override fun pickImage() {}
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker = JvmImagePicker()

class JvmVideoPicker : VideoPicker {
    override fun pickVideo() {}
}

@Composable
actual fun rememberVideoPicker(onVideoPicked: (Media.Video?) -> Unit): VideoPicker = JvmVideoPicker()

actual class PermissionsManager actual constructor(callback: PermissionCallback) : PermissionHandler {
    @Composable
    actual override fun AskPermission(permission: PermissionType) {}
    @Composable
    actual override fun isPermissionGranted(permission: PermissionType): Boolean = true
    @Composable
    actual override fun LaunchSettings() {}
}

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager = PermissionsManager(callback)
