package io.paku.climblog.data.source.remote

interface NotificationRemoteDataSource {
    suspend fun getNotifications(): List<io.paku.climblog.data.model.notification.NotificationData>
    suspend fun checkUnread(): Boolean
    suspend fun sendDeviceToken(fcmToken: String)
}
