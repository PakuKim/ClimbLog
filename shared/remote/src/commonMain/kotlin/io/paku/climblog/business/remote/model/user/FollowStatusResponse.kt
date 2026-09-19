package io.paku.climblog.business.remote.model.user

import kotlinx.serialization.Serializable

@Serializable
data class FollowStatusResponse(
    val isFollowing: Boolean
)
