package io.paku.climblog.domain.model

data class Comment(
    val id: Long = 0L,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: Long
)
