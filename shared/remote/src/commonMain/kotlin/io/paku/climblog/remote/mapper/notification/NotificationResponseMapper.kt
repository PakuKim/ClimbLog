package io.paku.climblog.remote.mapper.notification

import io.paku.climblog.contract.notification.NotificationResponse
import io.paku.climblog.core.BiMapper

internal object NotificationResponseMapper : BiMapper<NotificationResponse, io.paku.climblog.data.model.notification.NotificationData> {
    override fun mapToRight(from: NotificationResponse): io.paku.climblog.data.model.notification.NotificationData {
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

    override fun mapToLeft(from: io.paku.climblog.data.model.notification.NotificationData): NotificationResponse {
        return NotificationResponse(
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
