package io.paku.climblog.business.remote.dto.response.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    @SerialName("presignedUrl")
    val presignedUrl: String,
    @SerialName("s3Key")
    val s3Key: String
)

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
    @SerialName("cruxStartTime")
    val cruxStartTime: Double?,
    @SerialName("cruxEndTime")
    val cruxEndTime: Double?,
    @SerialName("createdAt")
    val createdAt: Long
)
