package io.paku.climblog.platform

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberVideoPicker(onVideoPicked: (Media.Video?) -> Unit): VideoPicker {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val videoMedia = uri?.let { readVideoUri(context, it) }
        onVideoPicked(videoMedia)
    }

    return remember {
        object : VideoPicker {
            override fun pickVideo() {
                launcher.launch("video/*")
            }
        }
    }
}

private fun readVideoUri(context: Context, uri: Uri): Media.Video {
    val contentResolver = context.contentResolver
    val name = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: "video.mp4"
    
    val contentType = contentResolver.getType(uri) ?: "video/mp4"
    
    return Media.Video(
        mimeType = contentType,
        fileName = name,
        source = PlatformMedia(uri, contentResolver)
    )
}
