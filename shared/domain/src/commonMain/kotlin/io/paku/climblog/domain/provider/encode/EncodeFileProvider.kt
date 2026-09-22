package io.paku.climblog.domain.provider.encode

interface EncodeFileProvider {
    suspend fun encodeImageFromUri(
        uri: String,
    ): io.paku.climblog.domain.model.EncodeResult

    suspend fun encodeFileFromUri(
        uri: String,
    ): io.paku.climblog.domain.model.EncodeResult

    suspend fun getFileSizeFromUri(
        uri: String,
    ): Long
}