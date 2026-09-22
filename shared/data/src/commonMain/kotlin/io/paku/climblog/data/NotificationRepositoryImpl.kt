package io.paku.climblog.data

internal class NotificationRepositoryImpl(
    private val remote: io.paku.climblog.data.source.remote.NotificationRemoteDataSource
) : io.paku.climblog.domain.NotificationRepository {

    override suspend fun getNotifications(): Result<List<io.paku.climblog.domain.model.Notification>> = runCatching {
        remote.getNotifications().map(io.paku.climblog.data.mapper.notification.NotificationDataMapper::mapToRight)
    }

    override suspend fun checkUnread(): Result<Boolean> = runCatching {
        remote.checkUnread()
    }

    override suspend fun sendDeviceToken(fcmToken: String): Result<Unit> = runCatching {
        remote.sendDeviceToken(fcmToken)
    }
}
