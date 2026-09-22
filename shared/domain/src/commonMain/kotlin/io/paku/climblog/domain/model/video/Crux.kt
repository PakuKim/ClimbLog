package io.paku.climblog.domain.model.video

data class Crux(
    val id: Long,
    val videoId: Long,
    val startTime: Double?,
    val endTime: Double?
)