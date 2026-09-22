package io.paku.climblog.data.mapper.notification

import io.paku.climblog.core.BiMapper

internal object NotificationDataMapper : BiMapper<io.paku.climblog.data.model.notification.NotificationData, io.paku.climblog.domain.model.Notification> {
    override fun mapToRight(from: io.paku.climblog.data.model.notification.NotificationData): io.paku.climblog.domain.model.Notification {
        return _root_ide_package_.io.paku.climblog.domain.model.Notification(
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

    override fun mapToLeft(from: io.paku.climblog.domain.model.Notification): io.paku.climblog.data.model.notification.NotificationData {
        return _root_ide_package_.io.paku.climblog.data.model.notification.NotificationData(
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
