package io.paku.climblog.business.remote.dto.response.video

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
    @SerialName("cruxes")
    val cruxes: List<CruxResponse>,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
)

