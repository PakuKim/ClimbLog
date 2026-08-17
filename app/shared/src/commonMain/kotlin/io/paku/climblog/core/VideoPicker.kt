package io.paku.climblog.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberVideoPicker(onVideoPicked: (VideoFile?) -> Unit): VideoPicker

interface VideoPicker {
    fun pickVideo()
}

data class VideoFile(
    val bytes: ByteArray,
    val name: String,
    val contentType: String,
    val previewUrl: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as VideoFile
        if (!bytes.contentEquals(other.bytes)) return false
        if (name != other.name) return false
        if (contentType != other.contentType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}
