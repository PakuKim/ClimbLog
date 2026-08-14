package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    val presignedUrl: String,
    val s3Key: String
)

@Serializable
data class VideoResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val cruxStartTime: Double?,
    val cruxEndTime: Double?,
    val createdAt: Long
)
