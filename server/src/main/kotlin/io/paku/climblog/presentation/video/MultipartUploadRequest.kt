package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class InitiateMultipartUploadRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val videoQuality: String
)

@Serializable
data class PartPresignedUrlRequest(
    val uploadId: String,
    val objectKey: String,
    val partNumbers: List<Int>
)

@Serializable
data class UploadedPart(
    val partNumber: Int,
    val eTag: String
)

@Serializable
data class CompleteMultipartUploadRequest(
    val uploadId: String,
    val objectKey: String,
    val parts: List<UploadedPart>,
    val title: String,
    val description: String?,
    val cruxStartTime: Double?,
    val cruxEndTime: Double?
)
