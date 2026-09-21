package io.paku.climblog.business.data.model.notification

import kotlinx.datetime.LocalDateTime

data class NotificationData(
    val id: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)
