package io.paku.climblog.domain

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<io.paku.climblog.domain.model.Notification>>
    suspend fun checkUnread(): Result<Boolean>
    suspend fun sendDeviceToken(fcmToken: String): Result<Unit>
}
