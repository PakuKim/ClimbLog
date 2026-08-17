package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.paku.climblog.business.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.business.domain.model.Notification
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class NotificationResponse(
    val id: Long,
    val type: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserProfilePhotoUrl: String?,
    val videoId: Long?,
    val isRead: Boolean,
    val createdAt: Long
)

@Serializable
data class UnreadCheckResponse(
    val hasUnread: Boolean
)

internal class NotificationRemoteDataSourceImpl(
    private val client: HttpClient
) : NotificationRemoteDataSource {

    override suspend fun getNotifications(): List<Notification> {
        return client.get("api/v1/notifications").body<List<NotificationResponse>>().map { it.toDomain() }
    }

    override suspend fun checkUnread(): Boolean {
        return client.get("api/v1/notifications/unread-check").body<UnreadCheckResponse>().hasUnread
    }

    override suspend fun sendDeviceToken(fcmToken: String) {
        client.post("api/v1/notifications/device-token") {
            setBody(buildJsonObject { put("fcmToken", fcmToken) })
        }
    }
}

private fun NotificationResponse.toDomain() = Notification(
    id = id,
    type = type,
    fromUserId = fromUserId,
    fromUserName = fromUserName,
    fromUserProfilePhotoUrl = fromUserProfilePhotoUrl,
    videoId = videoId,
    isRead = isRead,
    createdAt = createdAt
)
