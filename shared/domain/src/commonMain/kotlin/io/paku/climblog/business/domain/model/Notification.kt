package io.paku.climblog.business.domain.model

data class Notification(
    val id: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: Long
)
