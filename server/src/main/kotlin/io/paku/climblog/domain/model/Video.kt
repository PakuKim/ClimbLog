package io.paku.climblog.domain.model

data class Video(
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val createdAt: Long,
    val cruxStartTime: Double? = null,
    val cruxEndTime: Double? = null
)
