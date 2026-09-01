package io.paku.climblog.business.domain.model

data class Crux(
    val id: Long,
    val videoId: Long,
    val startTime: Double?,
    val endTime: Double?
)
