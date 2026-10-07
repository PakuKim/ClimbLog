package io.paku.climblog.contract.video

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VideoStatus {
    UPLOADING,
    PROCESSING,
    READY,
    FAILED
}

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
    val hlsUrl: String?,
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
data class PresignedPutResponse(
    @SerialName("uploadUrl")
    val uploadUrl: String,
    @SerialName("objectKey")
    val objectKey: String
)

@Serializable
data class PresignedPutRequest(
    @SerialName("fileName")
    val fileName: String,
    @SerialName("contentType")
    val contentType: String
)

@Serializable
data class RegisterVideoRequest(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String?,
    @SerialName("s3Key")
    val s3Key: String,
    @SerialName("cruxes")
    val cruxes: List<Crux> = emptyList()
) {
    @Serializable
    data class Crux(
        @SerialName("startTime")
        val startTime: Double,
        @SerialName("endTime")
        val endTime: Double
    )
}
