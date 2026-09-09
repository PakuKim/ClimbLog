package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.paku.climblog.business.data.source.remote.UserRemoteDataSource
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.user.UserProfile
import io.paku.climblog.business.remote.dto.request.user.RegisterUserInfoRequest
import io.paku.climblog.business.remote.dto.response.user.FollowStatusResponse
import io.paku.climblog.business.remote.dto.response.user.GetUserResponse
import io.paku.climblog.business.remote.dto.response.user.HandleCheckResponse
import io.paku.climblog.business.remote.dto.response.user.UserListResponse
import io.paku.climblog.business.remote.dto.response.user.UserProfileResponse

internal class UserRemoteDataSourceImpl(
    private val client: HttpClient
): UserRemoteDataSource {
    private companion object {
        const val GET_USER_URL = "users/me"
        const val CHECK_HANDLE_URL = "users/check/handle"
        const val SEARCH_URL = "users/search"
        const val PROFILE_URL = "users/{id}/profile"
        const val FOLLOW_URL = "users/{id}/follow"
        const val FOLLOW_STATUS_URL = "users/{id}/follow/status"
        const val FOLLOWERS_URL = "users/{id}/followers"
        const val FOLLOWING_URL = "users/{id}/following"
    }

    override suspend fun getUser(): User {
        return client.get(GET_USER_URL).body<GetUserResponse>().toDomain()
    }

    override suspend fun checkHandle(handle: String): Boolean {
        return client.get(CHECK_HANDLE_URL) {
            parameter("handle", handle)
        }.body<HandleCheckResponse>().exists
    }

    override suspend fun searchUsers(query: String): List<User> {
        return client.get(SEARCH_URL) {
            parameter("query", query)
        }.body<List<GetUserResponse>>().map { it.toDomain() }
    }

    override suspend fun getUserProfile(userId: Long): UserProfile {
        return client.get(PROFILE_URL.replace("{id}", userId.toString()))
            .body<UserProfileResponse>().toDomain()
    }

    override suspend fun follow(userId: Long) {
        client.post(FOLLOW_URL.replace("{id}", userId.toString()))
    }

    override suspend fun unfollow(userId: Long) {
        client.delete(FOLLOW_URL.replace("{id}", userId.toString()))
    }

    override suspend fun getFollowers(userId: Long): List<User> {
        return client.get(FOLLOWERS_URL.replace("{id}", userId.toString()))
            .body<UserListResponse>().users.map { it.toDomain() }
    }

    override suspend fun getFollowing(userId: Long): List<User> {
        return client.get(FOLLOWING_URL.replace("{id}", userId.toString()))
            .body<UserListResponse>().users.map { it.toDomain() }
    }

    override suspend fun getFollowStatus(userId: Long): Boolean {
        return client.get(FOLLOW_STATUS_URL.replace("{id}", userId.toString()))
            .body<FollowStatusResponse>().isFollowing
    }

    override suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): User {
        val request = RegisterUserInfoRequest(
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
        return client.put(GET_USER_URL) {
            setBody(request)
        }.body<GetUserResponse>().toDomain()
    }

    override suspend fun deleteUser() {
        client.delete(GET_USER_URL)
    }
}

private fun UserProfileResponse.toDomain() = UserProfile(
    user = user.toDomain(),
    followerCount = followerCount,
    followingCount = followingCount,
    videoCount = videoCount,
    isFollowing = isFollowing
)

private fun GetUserResponse.toDomain() = User(
    id = id,
    name = name,
    handle = handle,
    age = age,
    height = height,
    armReach = armReach,
    gender = gender,
    profilePhotoUrl = profilePhotoUrl
)
