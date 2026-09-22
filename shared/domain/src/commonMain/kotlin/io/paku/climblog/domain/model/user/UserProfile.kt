package io.paku.climblog.domain.model.user

data class UserProfile(
    val user: User,
    val followerCount: Long,
    val followingCount: Long,
    val videoCount: Long,
    val isFollowing: Boolean
)
