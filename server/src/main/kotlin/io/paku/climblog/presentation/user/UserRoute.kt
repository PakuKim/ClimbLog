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
import io.paku.climblog.domain.interactor.user.CompleteRegistrationUseCase
import io.paku.climblog.domain.interactor.user.DeleteUserUseCase
import io.paku.climblog.domain.interactor.user.FollowUserUseCase
import io.paku.climblog.domain.interactor.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactor.user.GetUserUseCase
import io.paku.climblog.domain.interactor.user.SearchUsersUseCase
import io.paku.climblog.domain.interactor.user.UnfollowUserUseCase
import io.paku.climblog.domain.interactor.user.UpdateUserUseCase
import io.paku.climblog.domain.model.User
import io.paku.climblog.domain.model.UserProfile
import io.paku.climblog.domain.model.Video
import io.paku.climblog.presentation.video.VideoResponse
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val getUserUseCase: GetUserUseCase by inject()
    val checkHandleUseCase: CheckHandleUseCase by inject()
    val completeRegistrationUseCase: CompleteRegistrationUseCase by inject()
    val searchUsersUseCase: SearchUsersUseCase by inject()
    val getUserProfileUseCase: GetUserProfileUseCase by inject()
    val followUserUseCase: FollowUserUseCase by inject()
    val unfollowUserUseCase: UnfollowUserUseCase by inject()
    val updateUserUseCase: UpdateUserUseCase by inject()
    val deleteUserUseCase: DeleteUserUseCase by inject()
    val videoRepository: VideoRepository by inject()

    route("/api/v1/users") {
        get("/check-handle") {
            val handle = call.request.queryParameters["handle"]
            if (handle == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Handle is required"))
                return@get
            }
            val exists = checkHandleUseCase(handle)
            call.respond(HttpStatusCode.OK, HandleCheckResponse(exists))
        }

        get("/search") {
            val query = call.request.queryParameters["query"] ?: ""
            searchUsersUseCase(query).onSuccess { users ->
                call.respond(HttpStatusCode.OK, users.map { it.toResponse() })
            }.onFailure {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        
        get("/{id}/profile") {
            val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val currentUserId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull()
            
            getUserProfileUseCase(targetId, currentUserId).onSuccess { profile ->
                call.respond(HttpStatusCode.OK, profile.toResponse())
            }.onFailure {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{id}/videos") {
            val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val videos = videoRepository.findAllByUserId(targetId)
            call.respond(HttpStatusCode.OK, videos.map { it.toResponse() })
        }

        authenticate("auth-jwt") {
            route("/me") {
                get {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    getUserUseCase(userId).onSuccess { user ->
                        call.respond(HttpStatusCode.OK, user.toResponse())
                    }.onFailure {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                put {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val request = call.receive<RegisterUserInfoRequest>()
                    updateUserUseCase(
                        userId = userId,
                        name = request.name,
                        age = request.age,
                        height = request.height,
                        armReach = request.armReach,
                        gender = request.gender,
                        profilePhotoUrl = request.profilePhotoUrl
                    ).onSuccess { user ->
                        call.respond(HttpStatusCode.OK, user.toResponse())
                    }.onFailure {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to (it.message ?: "")))
                    }
                }
                delete {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    deleteUserUseCase(userId).onSuccess {
                        call.respond(HttpStatusCode.NoContent)
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }

            route("/{id}/follow") {
                post {
                    val followerId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val followingId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    followUserUseCase(followerId, followingId).onSuccess {
                        call.respond(HttpStatusCode.OK, mapOf("isFollowing" to true))
                    }.onFailure {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to (it.message ?: "")))
                    }
                }

                delete {
                    val followerId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val followingId = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    unfollowUserUseCase(followerId, followingId).onSuccess {
                        call.respond(HttpStatusCode.OK, mapOf("isFollowing" to false))
                    }.onFailure {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to (it.message ?: "")))
                    }
                }
            }

            post("/register") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toLongOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid user ID format in token"))
                    return@post
                }

                val request = call.receive<RegisterUserInfoRequest>()
                completeRegistrationUseCase(
                    userId = userId,
                    handle = request.handle,
                    name = request.name,
                    age = request.age,
                    height = request.height,
                    armReach = request.armReach,
                    gender = request.gender,
                    profilePhotoUrl = request.profilePhotoUrl
                ).onSuccess { user ->
                    call.respond(HttpStatusCode.OK, user.toResponse())
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (it.message ?: "Unknown error")))
                }
            }
        }
    }
}

private fun User.toResponse() = UserResponse(
    id = id,
    email = email,
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
    cruxStartTime = cruxStartTime,
    cruxEndTime = cruxEndTime,
    createdAt = createdAt
)
