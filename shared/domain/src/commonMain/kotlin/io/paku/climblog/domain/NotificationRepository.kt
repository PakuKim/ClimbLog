package io.paku.climblog.domain

import io.paku.climblog.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun checkUnread(): Result<Boolean>
    suspend fun sendDeviceToken(fcmToken: String): Result<Unit>
}
