package io.paku.climblog.core

expect class PlatformMedia {
    suspend fun readBytes(): ByteArray
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