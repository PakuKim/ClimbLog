package io.paku.climblog.data

import io.paku.climblog.data.mapper.notification.NotificationDataMapper
import io.paku.climblog.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.model.Notification

internal class NotificationRepositoryImpl(
    private val remote: NotificationRemoteDataSource
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<Notification>> = runCatching {
        remote.getNotifications().map(NotificationDataMapper::mapToRight)
    }

    override suspend fun checkUnread(): Result<Boolean> = runCatching {
        remote.checkUnread()
    }

    override suspend fun sendDeviceToken(fcmToken: String): Result<Unit> = runCatching {
        remote.sendDeviceToken(fcmToken)
    }
}
