package io.paku.climblog.domain.model.video

import io.paku.climblog.domain.ext.now
import kotlinx.datetime.LocalDateTime

data class Video(
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val description: String,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val videoCruxes: List<VideoCrux> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
