package io.paku.climblog.presentation.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val handle: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val armReach: Int? = null,
    val gender: String? = null,
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
