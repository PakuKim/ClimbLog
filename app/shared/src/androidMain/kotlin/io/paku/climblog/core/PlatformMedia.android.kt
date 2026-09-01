package io.paku.climblog.core

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class PlatformMedia(
    private val uri: Uri,
    private val contentResolver: ContentResolver
) {
    actual suspend fun readBytes(): ByteArray = withContext(Dispatchers.IO) {
        contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw Exception("Failed to read bytes from $uri")
    }
}