package io.paku.climblog.business.domain.provider.encode

import io.paku.climblog.business.domain.model.EncodeResult

interface EncodeFileProvider {
    suspend fun encodeImageFromUri(
        uri: String,
    ): EncodeResult

    suspend fun encodeFileFromUri(
        uri: String,
    ): EncodeResult

    suspend fun getFileSizeFromUri(
        uri: String,
    ): Long
}