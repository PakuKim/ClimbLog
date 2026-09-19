package io.paku.climblog.business.data.model.user

data class UserProfileData(
    val user: UserData,
    val followerCount: Long,
    val followingCount: Long,
    val videoCount: Long,
    val isFollowing: Boolean
)
