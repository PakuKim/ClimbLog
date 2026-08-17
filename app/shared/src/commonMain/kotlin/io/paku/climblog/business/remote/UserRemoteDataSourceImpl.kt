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
import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile
import io.paku.climblog.business.remote.dto.request.user.RegisterUserInfoRequest
import io.paku.climblog.business.remote.dto.response.user.GetUserResponse
import io.paku.climblog.business.remote.dto.response.user.HandleCheckResponse
import io.paku.climblog.business.remote.dto.response.user.UserProfileResponse

internal class UserRemoteDataSourceImpl(
    private val client: HttpClient
): UserRemoteDataSource {
    private companion object {
        const val API_BASE = "api/v1/users"
        const val GET_USER_URL = "api/v1/users/me" 
        const val CHECK_HANDLE_URL = "$API_BASE/check-handle"
        const val SEARCH_URL = "$API_BASE/search"
        const val PROFILE_URL = "$API_BASE/{id}/profile"
        const val FOLLOW_URL = "$API_BASE/{id}/follow"
        const val REGISTER_URL = "$API_BASE/register"
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

    override suspend fun updateUser(
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): User {
        val request = RegisterUserInfoRequest(
            handle = "", // Server ignores handle for PUT /me
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

    override suspend fun registerUser(
        handle: String,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): User {
        val request = RegisterUserInfoRequest(
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
        return client.post(REGISTER_URL) {
            setBody(request)
        }.body<GetUserResponse>().toDomain()
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
    email = email,
    name = name,
    handle = handle,
    age = age,
    height = height,
    armReach = armReach,
    gender = gender,
    profilePhotoUrl = profilePhotoUrl
)
