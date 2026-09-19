package io.paku.climblog.business.data.mapper.notification

import io.paku.climblog.business.data.model.notification.NotificationData
import io.paku.climblog.business.domain.model.Notification
import io.paku.climblog.core.BiMapper

internal object NotificationDataMapper : BiMapper<NotificationData, Notification> {
    override fun mapToRight(from: NotificationData): Notification {
        return Notification(
            id = from.id,
            type = from.type,
            fromUserId = from.fromUserId,
            fromUserName = from.fromUserName,
            fromUserProfilePhotoUrl = from.fromUserProfilePhotoUrl,
            videoId = from.videoId,
            isRead = from.isRead,
            createdAt = from.createdAt
        )
    }

    override fun mapToLeft(from: Notification): NotificationData {
        return NotificationData(
            id = from.id,
            type = from.type,
            fromUserId = from.fromUserId,
            fromUserName = from.fromUserName,
            fromUserProfilePhotoUrl = from.fromUserProfilePhotoUrl,
            videoId = from.videoId,
            isRead = from.isRead,
            createdAt = from.createdAt
        )
    }
}
