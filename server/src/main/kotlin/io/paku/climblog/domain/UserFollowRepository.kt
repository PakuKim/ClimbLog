package io.paku.climblog.domain

interface UserFollowRepository {
    suspend fun follow(followerId: Long, followingId: Long): Boolean
    suspend fun unfollow(followerId: Long, followingId: Long): Boolean
    suspend fun isFollowing(followerId: Long, followingId: Long): Boolean
    suspend fun getFollowerCount(userId: Long): Long
    suspend fun getFollowingCount(userId: Long): Long
}
