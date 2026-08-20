package io.paku.climblog.presentation.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.user.CheckHandleUseCase
import io.paku.climblog.domain.interactor.user.DeleteUserUseCase
import io.paku.climblog.domain.interactor.user.FollowUserUseCase
import io.paku.climblog.domain.interactor.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactor.user.GetUserUseCase
import io.paku.climblog.domain.interactor.user.SearchUsersUseCase
import io.paku.climblog.domain.interactor.user.UnfollowUserUseCase
import io.paku.climblog.domain.interactor.user.UpdateUserUseCase
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.user.UserProfile
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.presentation.video.VideoResponse
import org.koin.ktor.ext.inject

internal fun Route.userRoutes() {
    val getUserUseCase: GetUserUseCase by inject()
    val checkHandleUseCase: CheckHandleUseCase by inject()
    val searchUsersUseCase: SearchUsersUseCase by inject()
    val getUserProfileUseCase: GetUserProfileUseCase by inject()
    val followUserUseCase: FollowUserUseCase by inject()
    val unfollowUserUseCase: UnfollowUserUseCase by inject()
    val updateUserUseCase: UpdateUserUseCase by inject()
    val deleteUserUseCase: DeleteUserUseCase by inject()
    val videoRepository: VideoRepository by inject()

    route("/api/v1/users") {
        get("/check/handle") {
            val handle = call.request.queryParameters["handle"]
            if (handle == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Handle is required"))
                return@get
            }
            val exists = checkHandleUseCase(handle)
            call.respond(HttpStatusCode.OK, HandleCheckResponse(exists))
        }

        authenticate("auth-jwt") {
            route("/me") {
                get {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val user = getUserUseCase(userId)

                    call.respond(HttpStatusCode.OK, user.toResponse())
                }
                put {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val request = call.receive<UpdateUserRequest>()
                    val user = updateUserUseCase(
                        userId = userId,
                        name = request.name,
                        age = request.age,
                        height = request.height,
                        armReach = request.armReach,
                        gender = request.gender,
                        profilePhotoUrl = request.profilePhotoUrl
                    )

                    call.respond(HttpStatusCode.OK, user.toResponse())
                }
                delete {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    deleteUserUseCase(userId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/{id}/follow") {
                post {
                    val followerId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val followingId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    followUserUseCase(followerId, followingId)
                    call.respond(HttpStatusCode.OK, mapOf("isFollowing" to true))
                }

                delete {
                    val followerId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val followingId = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    unfollowUserUseCase(followerId, followingId)
                    call.respond(HttpStatusCode.OK, mapOf("isFollowing" to false))
                }
            }

            get("/search") {
                val query = call.request.queryParameters["query"] ?: ""
                val users = searchUsersUseCase(query)
                call.respond(HttpStatusCode.OK, users.map { it.toResponse() })
            }

            get("/{id}/profile") {
                val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val currentUserId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull()

                val profile = getUserProfileUseCase(targetId, currentUserId)
                call.respond(HttpStatusCode.OK, profile.toResponse())
            }

            get("/{id}/videos") {
                val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val videos = videoRepository.findAllByUserId(targetId)
                call.respond(HttpStatusCode.OK, videos.map { it.toResponse() })
            }
        }
    }
}

private fun User.toResponse() = UserResponse(
    id = id,
    name = name,
    handle = handle,
    age = age,
    height = height,
    armReach = armReach,
    gender = gender,
    profilePhotoUrl = profilePhotoUrl
)

private fun UserProfile.toResponse() = UserProfileResponse(
    user = user.toResponse(),
    followerCount = followerCount,
    followingCount = followingCount,
    videoCount = videoCount,
    isFollowing = isFollowing
)

private fun Video.toResponse() = VideoResponse(
    id = id,
    userId = userId,
    title = title,
    description = description,
    hlsUrl = hlsUrl,
    thumbnailUrl = thumbnailUrl,
    cruxes = videoCruxes.map {
        VideoResponse.Crux(
            id = it.id,
            cruxStartTime = it.startTime,
            cruxEndTime = it.endTime
        )
    },
    createdAt = createdAt
)
