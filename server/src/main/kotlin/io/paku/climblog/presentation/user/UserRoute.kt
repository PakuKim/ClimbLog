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
import io.paku.climblog.contract.user.FollowStatusResponse
import io.paku.climblog.contract.user.HandleCheckResponse
import io.paku.climblog.contract.user.UserListResponse
import io.paku.climblog.contract.user.UserProfileResponse
import io.paku.climblog.contract.user.UserRequest
import io.paku.climblog.contract.user.UserResponse
import io.paku.climblog.domain.ext.getUserId
import io.paku.climblog.domain.interactor.user.CheckHandleUseCase
import io.paku.climblog.domain.interactor.user.DeleteUserUseCase
import io.paku.climblog.domain.interactor.user.FollowUserUseCase
import io.paku.climblog.domain.interactor.user.GetFollowStatusUseCase
import io.paku.climblog.domain.interactor.user.GetFollowersUseCase
import io.paku.climblog.domain.interactor.user.GetFollowingUseCase
import io.paku.climblog.domain.interactor.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactor.user.GetUserUseCase
import io.paku.climblog.domain.interactor.user.SearchUsersUseCase
import io.paku.climblog.domain.interactor.user.UnfollowUserUseCase
import io.paku.climblog.domain.interactor.user.UpdateUserUseCase
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.user.UserProfile
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
    val getFollowersUseCase: GetFollowersUseCase by inject()
    val getFollowingUseCase: GetFollowingUseCase by inject()
    val getFollowStatusUseCase: GetFollowStatusUseCase by inject()

    route("/api/v1/users") {
        get("/check/handle") {
            val handle = call.request.queryParameters["handle"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Handle is required"))

            val exists = checkHandleUseCase(handle)
            call.respond(HttpStatusCode.OK, HandleCheckResponse(exists))
        }

        authenticate("auth-jwt") {
            route("/me") {
                get {
                    val userId = call.getUserId()
                    val user = getUserUseCase(userId)

                    call.respond(HttpStatusCode.OK, user.toResponse())
                }

                put {
                    val userId = call.getUserId()
                    val request = call.receive<UserRequest>()
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
                    val userId = call.getUserId()
                    deleteUserUseCase(userId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/{id}/follow") {
                get("/status") {
                    val userId = call.getUserId()
                    val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

                    getFollowStatusUseCase(userId, targetId).onSuccess { isFollowing ->
                        call.respond(HttpStatusCode.OK, FollowStatusResponse(isFollowing))
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }

                post {
                    val userId = call.getUserId()
                    val targetId = call.parameters["id"]?.toLongOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    followUserUseCase(userId, targetId)
                    call.respond(HttpStatusCode.OK, mapOf("isFollowing" to true))
                }

                delete {
                    val userId = call.getUserId()
                    val targetId = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    unfollowUserUseCase(userId, targetId)
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
            route("/{id}/followers") {
                get {
                    val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    getFollowersUseCase(targetId).onSuccess { followers ->
                        call.respond(HttpStatusCode.OK, UserListResponse(followers.map { it.toResponse() }))
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }

            route("/{id}/following") {
                get {
                    val targetId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    getFollowingUseCase(targetId).onSuccess { following ->
                        call.respond(HttpStatusCode.OK, UserListResponse(following.map { it.toResponse() }))
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
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
