package io.paku.climblog.presentation.video

import kotlinx.datetime.LocalDateTime
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
    val description: String,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val cruxes: List<Crux>,
    val createdAt: LocalDateTime
) {
    @Serializable
    data class Crux(
        val id: Long,
        val cruxStartTime: Double,
        val cruxEndTime: Double,
    )
}
