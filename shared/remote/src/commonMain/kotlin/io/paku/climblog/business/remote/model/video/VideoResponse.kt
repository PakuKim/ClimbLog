package io.paku.climblog.business.remote.model.video

import io.paku.climblog.business.domain.model.video.VideoStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("userId")
    val userId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String?,
    @SerialName("hlsUrl")
    val hlsUrl: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerialName("status")
    val status: VideoStatus,
    @SerialName("cruxes")
    val cruxes: List<CruxResponse>,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
)

@Serializable
data class CruxResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("cruxStartTime")
    val cruxStartTime: Double?,
    @SerialName("cruxEndTime")
    val cruxEndTime: Double?
)

@Serializable
data class VideoFeedResponse(
    @SerialName("items")
    val items: List<VideoResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long?
)

@Serializable
data class PresignedPostResponse(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)
