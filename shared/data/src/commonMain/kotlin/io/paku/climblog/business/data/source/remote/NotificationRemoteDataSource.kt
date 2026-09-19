package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.data.model.notification.NotificationData

interface NotificationRemoteDataSource {
    suspend fun getNotifications(): List<NotificationData>
    suspend fun checkUnread(): Boolean
    suspend fun sendDeviceToken(fcmToken: String)
}
