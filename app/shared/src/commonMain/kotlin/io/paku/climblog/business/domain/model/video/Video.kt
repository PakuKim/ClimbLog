package io.paku.climblog.business.domain.model.video

import kotlinx.datetime.LocalDateTime

data class Video(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val status: VideoStatus = VideoStatus.UPLOADING,
    val createdAt: LocalDateTime,
    val cruxes: List<Crux> = emptyList()
)
