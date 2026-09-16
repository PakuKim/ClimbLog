package io.paku.climblog.presentation.video

import io.paku.climblog.domain.model.video.VideoStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PresignedPostResponse(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)

@Serializable
data class VideoFeedResponse(
    val items: List<VideoResponse>,
    val nextCursor: Long?
)

@Serializable
data class VideoResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val status: VideoStatus,
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
