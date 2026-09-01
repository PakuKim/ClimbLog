package io.paku.climblog.core

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberGalleryManager(
    onResult: (Media?) -> Unit
): GalleryManager {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }

        val mimeType = contentResolver.getType(uri) ?: run {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        val source = PlatformMedia(
            uri = uri,
            contentResolver = contentResolver
        )
        val media = when {
            mimeType.startsWith("image/") -> {
                Media.Image(
                    source = source,
                    mimeType = mimeType,
                    fileName = "image"
                )
            }
            mimeType.startsWith("video/") -> {
                Media.Video(
                    source = source,
                    mimeType = mimeType,
                    fileName = "video"
                )
            }
            else -> null
        }

        onResult(media)

//        uri.let {
//            onResult.invoke(PlatformMedia(BitmapUtil.getBitmapFromUri(uri, contentResolver)))
//        }
    }

    return remember {
        GalleryManager(
            onLaunch = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    )
                )
            }
        )
    }
}

actual class GalleryManager actual constructor(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}