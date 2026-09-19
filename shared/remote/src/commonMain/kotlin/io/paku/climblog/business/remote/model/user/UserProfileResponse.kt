package io.paku.climblog.business.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
