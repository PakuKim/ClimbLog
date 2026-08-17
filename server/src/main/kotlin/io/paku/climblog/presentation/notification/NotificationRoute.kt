package io.paku.climblog.presentation.notification

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.model.Notification
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class DeviceTokenRequest(val fcmToken: String)

fun Route.notificationRoutes() {
    val notificationRepository: NotificationRepository by inject()

    authenticate("auth-jwt") {
        route("/api/v1/notifications") {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val notifications = notificationRepository.findAllByUserId(userId)
                call.respond(HttpStatusCode.OK, notifications.map { it.toResponse() })
                notificationRepository.markAsRead(userId)
            }

            get("/unread-check") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val hasUnread = notificationRepository.hasUnread(userId)
                call.respond(HttpStatusCode.OK, UnreadCheckResponse(hasUnread))
            }

            post("/device-token") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<DeviceTokenRequest>()
                notificationRepository.saveDeviceToken(userId, request.fcmToken)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private fun Notification.toResponse() = NotificationResponse(
    id = id,
    type = type,
    fromUserId = fromUserId,
    fromUserName = fromUserName,
    fromUserProfilePhotoUrl = fromUserProfilePhotoUrl,
    videoId = videoId,
    isRead = isRead,
    createdAt = createdAt
)
