package io.paku.climblog.business.domain.model

data class Video(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val hlsUrl: String,
    val thumbnailUrl: String?,
    val createdAt: Long,
    val cruxes: List<Crux> = emptyList()
)
