package io.paku.climblog.business.domain.model

data class EncodeResult(
    val byteArray: ByteArray,
    val mimeType: String,
    val width: Int,
    val height: Int
) {
    override fun hashCode(): Int {
        var result = byteArray.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncodeResult

        if (width != other.width) return false
        if (height != other.height) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (mimeType != other.mimeType) return false

        return true
    }
}