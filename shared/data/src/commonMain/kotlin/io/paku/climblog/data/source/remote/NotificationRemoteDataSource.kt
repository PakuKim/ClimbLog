package io.paku.climblog.data.source.remote

import io.paku.climblog.data.model.notification.NotificationData

interface NotificationRemoteDataSource {
    suspend fun getNotifications(): List<NotificationData>
    suspend fun checkUnread(): Boolean
    suspend fun sendDeviceToken(fcmToken: String)
}
