package io.paku.climblog.business.domain.model

data class Comment(
    val id: Long,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: Long
)
