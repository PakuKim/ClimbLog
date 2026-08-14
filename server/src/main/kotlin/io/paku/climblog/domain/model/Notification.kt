package io.paku.climblog.domain.model

data class Notification(
    val id: Long = 0L,
    val userId: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: Long
)
