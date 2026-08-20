package io.paku.climblog.presentation.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val handle: String,
    val age: Int,
    val height: Int,
    val armReach: Int,
    val gender: String,
    val profilePhotoUrl: String? = null
)

@Serializable
data class UserProfileResponse(
    val user: UserResponse,
    val followerCount: Long,
    val followingCount: Long,
    val videoCount: Long,
    val isFollowing: Boolean
)

@Serializable
data class HandleCheckResponse(
    val exists: Boolean
)
