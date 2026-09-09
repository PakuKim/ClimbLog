package io.paku.climblog.business.remote.dto.response.user

import kotlinx.serialization.Serializable

@Serializable
data class FollowStatusResponse(
    val isFollowing: Boolean
)
