package io.paku.climblog.contract.notification

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("type")
    val type: String,
    @SerialName("fromUserId")
    val fromUserId: Long,
    @SerialName("fromUserName")
    val fromUserName: String,
    @SerialName("fromUserProfilePhotoUrl")
    val fromUserProfilePhotoUrl: String?,
    @SerialName("videoId")
    val videoId: Long?,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
)

@Serializable
data class UnreadCheckResponse(
    @SerialName("hasUnread")
    val hasUnread: Boolean
)
