package io.paku.climblog.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.paku.climblog.contract.notification.NotificationResponse
import io.paku.climblog.contract.notification.UnreadCheckResponse
import io.paku.climblog.data.model.notification.NotificationData
import io.paku.climblog.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.remote.mapper.notification.NotificationResponseMapper
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class NotificationRemoteDataSourceImpl(
    private val client: HttpClient
) : NotificationRemoteDataSource {

    override suspend fun getNotifications(): List<NotificationData> {
        return client.get("api/v1/notifications")
            .body<List<NotificationResponse>>()
            .map(NotificationResponseMapper::mapToRight)
    }

    override suspend fun checkUnread(): Boolean {
        return client.get("api/v1/notifications/unread-check")
            .body<UnreadCheckResponse>()
            .hasUnread
    }

    override suspend fun sendDeviceToken(fcmToken: String) {
        client.post("api/v1/notifications/device-token") {
            setBody(buildJsonObject { put("fcmToken", fcmToken) })
        }
    }
}
