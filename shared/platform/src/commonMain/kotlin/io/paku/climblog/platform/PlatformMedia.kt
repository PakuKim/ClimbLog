package io.paku.climblog.platform

import io.paku.climblog.domain.model.MediaSource

expect class PlatformMedia : MediaSource {
    override suspend fun readBytes(): ByteArray
    override suspend fun readChunk(start: Long, length: Int): ByteArray
    override suspend fun getSize(): Long
}

sealed interface Media {
    val mimeType: String
    val fileName: String

    data class Image(
        override val mimeType: String,
        override val fileName: String,
        val source: PlatformMedia
    ): Media

    data class Video(
        override val mimeType: String,
        override val fileName: String,
        val source: PlatformMedia
    ): Media
}