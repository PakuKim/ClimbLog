package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class VideoFeedResponse(
    val items: List<VideoResponse>,
    val nextCursor: Long?
)
