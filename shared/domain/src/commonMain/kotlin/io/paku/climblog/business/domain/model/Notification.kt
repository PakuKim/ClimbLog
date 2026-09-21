package io.paku.climblog.business.domain.model

import kotlinx.datetime.LocalDateTime

data class Notification(
    val id: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)
