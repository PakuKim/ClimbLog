package io.paku.climblog.core

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberVideoPicker(onVideoPicked: (VideoFile?) -> Unit): VideoPicker {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        scope.launch {
            val videoFile = uri?.let { readVideoUri(context, it) }
            onVideoPicked(videoFile)
        }
    }

    return remember {
        object : VideoPicker {
            override fun pickVideo() {
                launcher.launch("video/*")
            }
        }
    }
}

private suspend fun readVideoUri(context: Context, uri: Uri): VideoFile? = withContext(Dispatchers.IO) {
    val contentResolver = context.contentResolver
    val name = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: "video.mp4"
    
    val contentType = contentResolver.getType(uri) ?: "video/mp4"
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
    
    VideoFile(bytes, name, contentType, uri.toString())
}
