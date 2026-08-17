package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.Notification

interface NotificationRemoteDataSource {
    suspend fun getNotifications(): List<Notification>
    suspend fun checkUnread(): Boolean
    suspend fun sendDeviceToken(fcmToken: String)
}
