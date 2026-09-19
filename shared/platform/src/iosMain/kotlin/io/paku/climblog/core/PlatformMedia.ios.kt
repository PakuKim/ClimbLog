package io.paku.climblog.core

import io.paku.climblog.business.domain.model.MediaSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.fileHandleForReadingFromURL
import platform.posix.memcpy

actual class PlatformMedia(
    val url: NSURL
) : MediaSource {
    actual override suspend fun readBytes(): ByteArray {
        val data = NSData.dataWithContentsOfURL(url) ?: throw Exception("Failed to read bytes from $url")
        return data.toByteArray()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun readChunk(start: Long, length: Int): ByteArray {
        val fileHandle = NSFileHandle.fileHandleForReadingFromURL(url, null) ?: throw Exception("Failed to open file $url")
        fileHandle.seekToOffset(start.toULong(), null)
        val data = fileHandle.readDataUpToLength(length.toULong(), null) ?: throw Exception("Failed to read chunk")
        fileHandle.closeAndReturnError(null)
        return data.toByteArray()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun getSize(): Long {
        val fileManager = NSFileManager.defaultManager
        val attributes = fileManager.attributesOfItemAtPath(url.path!!, null) ?: throw Exception("Failed to get attributes for $url")
        return (attributes[NSFileSize] as? Long) ?: 0L
    }

    @OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
    private fun NSData.toByteArray(): ByteArray {
        val bytes = this.bytes
        val length = this.length
        return ByteArray(length.toInt()).apply {
            if (length > 0u) {
                usePinned { pinned ->
                    memcpy(pinned.addressOf(0), bytes, length)
                }
            }
        }
    }
}
