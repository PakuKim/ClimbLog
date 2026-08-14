package io.paku.climblog.business.remote.dto.response.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoFeedResponse(
    @SerialName("items")
    val items: List<VideoResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long?
)
