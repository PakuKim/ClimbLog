package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun checkUnread(): Result<Boolean>
    suspend fun sendDeviceToken(fcmToken: String): Result<Unit>
}
