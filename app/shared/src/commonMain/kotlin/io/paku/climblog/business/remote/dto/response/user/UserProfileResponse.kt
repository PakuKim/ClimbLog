package io.paku.climblog.business.remote.dto.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("user")
    val user: GetUserResponse,
    @SerialName("followerCount")
    val followerCount: Long,
    @SerialName("followingCount")
    val followingCount: Long,
    @SerialName("videoCount")
    val videoCount: Long,
    @SerialName("isFollowing")
    val isFollowing: Boolean
)
