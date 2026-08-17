package io.paku.climblog.domain

import io.paku.climblog.domain.model.Notification

interface NotificationRepository {
    suspend fun findAllByUserId(userId: Long): List<Notification>
    suspend fun hasUnread(userId: Long): Boolean
    suspend fun markAsRead(userId: Long)
    suspend fun save(notification: Notification): Notification
    
    suspend fun saveDeviceToken(userId: Long, fcmToken: String)
    suspend fun getDeviceToken(userId: Long): String?
}
