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
import io.paku.climblog.contract.notification.NotificationResponse
import io.paku.climblog.contract.notification.UnreadCheckResponse
import io.paku.climblog.domain.interactor.notification.CheckUnreadNotificationsUseCase
import io.paku.climblog.domain.interactor.notification.GetNotificationsUseCase
import io.paku.climblog.domain.interactor.notification.MarkNotificationsAsReadUseCase
import io.paku.climblog.domain.interactor.notification.SaveDeviceTokenUseCase
import io.paku.climblog.domain.model.Notification
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class DeviceTokenRequest(val fcmToken: String)

fun Route.notificationRoutes() {
    val getNotificationsUseCase: GetNotificationsUseCase by inject()
    val checkUnreadNotificationsUseCase: CheckUnreadNotificationsUseCase by inject()
    val saveDeviceTokenUseCase: SaveDeviceTokenUseCase by inject()
    val markNotificationsAsReadUseCase: MarkNotificationsAsReadUseCase by inject()

    authenticate("auth-jwt") {
        route("/api/v1/notifications") {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                getNotificationsUseCase(userId).onSuccess { notifications ->
                    call.respond(HttpStatusCode.OK, notifications.map { it.toResponse() })
                    markNotificationsAsReadUseCase(userId)
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            get("/unread-check") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                checkUnreadNotificationsUseCase(userId).onSuccess { hasUnread ->
                    call.respond(HttpStatusCode.OK, UnreadCheckResponse(hasUnread))
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post("/device-token") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<DeviceTokenRequest>()
                saveDeviceTokenUseCase(userId, request.fcmToken).onSuccess {
                    call.respond(HttpStatusCode.OK)
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
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
