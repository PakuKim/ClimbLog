package io.paku.climblog.business.domain.model

data class UserProfile(
    val user: User,
    val followerCount: Long,
    val followingCount: Long,
    val videoCount: Long,
    val isFollowing: Boolean
)
