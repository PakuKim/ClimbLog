package io.paku.climblog.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

actual class PlatformMedia(
    private val data: NSData
) {
    actual suspend fun readBytes(): ByteArray {
        return data.toByteArray()
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