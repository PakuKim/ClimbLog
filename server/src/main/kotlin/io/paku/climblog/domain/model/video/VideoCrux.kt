package io.paku.climblog.domain.model.video

data class VideoCrux(
    val id: Long = 0L,
    val videoId: Long = 0L,
    val startTime: Double,
    val endTime: Double
)
