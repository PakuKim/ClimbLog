package io.paku.climblog.contract.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("handle")
    val handle: String,
    @SerialName("age")
    val age: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("armReach")
    val armReach: Int,
    @SerialName("gender")
    val gender: String,
    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)

@Serializable
data class UserProfileResponse(
    @SerialName("user")
    val user: UserResponse,
    @SerialName("followerCount")
    val followerCount: Long,
    @SerialName("followingCount")
    val followingCount: Long,
    @SerialName("videoCount")
    val videoCount: Long,
    @SerialName("isFollowing")
    val isFollowing: Boolean
)

@Serializable
data class UserListResponse(
    @SerialName("users")
    val users: List<UserResponse>
)

@Serializable
data class FollowStatusResponse(
    @SerialName("isFollowing")
    val isFollowing: Boolean
)

@Serializable
data class HandleCheckResponse(
    @SerialName("exists")
    val exists: Boolean
)

@Serializable
data class UserRequest(
    @SerialName("handle")
    val handle: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("armReach")
    val armReach: Int? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)
