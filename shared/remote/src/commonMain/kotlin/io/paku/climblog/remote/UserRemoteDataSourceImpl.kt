package io.paku.climblog.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.paku.climblog.contract.user.FollowStatusResponse
import io.paku.climblog.contract.user.HandleCheckResponse
import io.paku.climblog.contract.user.UserListResponse
import io.paku.climblog.contract.user.UserProfileResponse
import io.paku.climblog.contract.user.UserRequest
import io.paku.climblog.contract.user.UserResponse
import io.paku.climblog.data.model.user.UserData
import io.paku.climblog.data.model.user.UserProfileData
import io.paku.climblog.data.source.remote.UserRemoteDataSource
import io.paku.climblog.remote.mapper.user.UserResponseMapper

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

    override suspend fun getUser(): UserData {
        return client.get(GET_USER_URL).body<UserResponse>().let { UserResponseMapper.mapToRight(it) }
    }

    override suspend fun checkHandle(handle: String): Boolean {
        return client.get(CHECK_HANDLE_URL) {
            parameter("handle", handle)
        }.body<HandleCheckResponse>().exists
    }

    override suspend fun searchUsers(query: String): List<UserData> {
        return client.get(SEARCH_URL) {
            parameter("query", query)
        }.body<List<UserResponse>>().map { UserResponseMapper.mapToRight(it) }
    }

    override suspend fun getUserProfile(userId: Long): UserProfileData {
        val response = client.get(PROFILE_URL.replace("{id}", userId.toString()))
            .body<UserProfileResponse>()
        
        return UserProfileData(
            user = UserResponseMapper.mapToRight(response.user),
            followerCount = response.followerCount,
            followingCount = response.followingCount,
            videoCount = response.videoCount,
            isFollowing = response.isFollowing
        )
    }

    override suspend fun follow(userId: Long) {
        client.post(FOLLOW_URL.replace("{id}", userId.toString()))
    }

    override suspend fun unfollow(userId: Long) {
        client.delete(FOLLOW_URL.replace("{id}", userId.toString()))
    }

    override suspend fun getFollowers(userId: Long): List<UserData> {
        return client.get(FOLLOWERS_URL.replace("{id}", userId.toString()))
            .body<UserListResponse>().users.map { UserResponseMapper.mapToRight(it) }
    }

    override suspend fun getFollowing(userId: Long): List<UserData> {
        return client.get(FOLLOWING_URL.replace("{id}", userId.toString()))
            .body<UserListResponse>().users.map { UserResponseMapper.mapToRight(it) }
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
    ): UserData {
        val request = UserRequest(
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
        return client.put(GET_USER_URL) {
            setBody(request)
        }.body<UserResponse>().let { UserResponseMapper.mapToRight(it) }
    }

    override suspend fun deleteUser() {
        client.delete(GET_USER_URL)
    }
}
