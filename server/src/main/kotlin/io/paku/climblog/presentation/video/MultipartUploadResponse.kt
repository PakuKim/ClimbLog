package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class InitiateMultipartUploadResponse(
    val uploadId: String,
    val objectKey: String,
    val partSize: Long
)

@Serializable
data class PartPresignedUrlResponse(
    val presignedUrls: Map<Int, String>
)
