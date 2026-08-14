package io.paku.climblog.presentation.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: Long
)

@Serializable
data class UnreadCheckResponse(
    val hasUnread: Boolean
)
