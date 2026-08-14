package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.business.domain.model.Notification

internal class NotificationRepositoryImpl(
    private val remote: NotificationRemoteDataSource
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<Notification>> = runCatching {
        remote.getNotifications()
    }

    override suspend fun checkUnread(): Result<Boolean> = runCatching {
        remote.checkUnread()
    }
}
