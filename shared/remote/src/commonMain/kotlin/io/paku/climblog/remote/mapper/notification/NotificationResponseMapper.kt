package io.paku.climblog.remote.mapper.notification

import io.paku.climblog.contract.notification.NotificationResponse
import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.notification.NotificationData

internal object NotificationResponseMapper : BiMapper<NotificationResponse, NotificationData> {
    override fun mapToRight(from: NotificationResponse): NotificationData {
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

    override fun mapToLeft(from: NotificationData): NotificationResponse {
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
