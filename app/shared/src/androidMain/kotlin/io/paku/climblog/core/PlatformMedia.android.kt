package io.paku.climblog.core

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class PlatformMedia(
    val uri: Uri,
    val contentResolver: ContentResolver
) {
    actual suspend fun readBytes(): ByteArray = withContext(Dispatchers.IO) {
        contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw Exception("Failed to read bytes from $uri")
    }

    actual suspend fun readChunk(start: Long, length: Int): ByteArray = withContext(Dispatchers.IO) {
        contentResolver.openInputStream(uri)?.use { input ->
            input.skip(start)
            val buffer = ByteArray(length)
            val read = input.read(buffer)
            if (read == length) buffer else buffer.copyOf(read)
        } ?: throw Exception("Failed to read chunk from $uri")
    }

    actual suspend fun getSize(): Long = withContext(Dispatchers.IO) {
        contentResolver.openAssetFileDescriptor(uri, "r")?.use {
            it.length
        } ?: throw Exception("Failed to get size for $uri")
    }
}
