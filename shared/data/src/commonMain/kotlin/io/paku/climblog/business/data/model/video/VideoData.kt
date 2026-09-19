package io.paku.climblog.business.data.model.video

import io.paku.climblog.business.domain.model.video.VideoStatus
import kotlinx.datetime.LocalDateTime

data class VideoData(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val status: VideoStatus,
    val createdAt: LocalDateTime,
    val cruxes: List<CruxData> = emptyList()
)

data class CruxData(
    val id: Long,
    val videoId: Long,
    val startTime: Double?,
    val endTime: Double?
)

data class VideoFeedData(
    val items: List<VideoData>,
    val nextCursor: Long?
)
