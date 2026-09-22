package io.paku.climblog.data.model.video

import kotlinx.datetime.LocalDateTime

data class VideoData(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val status: io.paku.climblog.domain.model.video.VideoStatus,
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
