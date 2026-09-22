package io.paku.climblog.domain.model

interface MediaSource {
    suspend fun readBytes(): ByteArray
    suspend fun readChunk(start: Long, length: Int): ByteArray
    suspend fun getSize(): Long
}
