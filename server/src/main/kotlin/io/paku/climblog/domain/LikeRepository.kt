package io.paku.climblog.domain

interface LikeRepository {
    suspend fun toggleLike(userId: Long, videoId: Long): Boolean // returns true if liked, false if unliked
    suspend fun isLiked(userId: Long, videoId: Long): Boolean
    suspend fun getLikeCount(videoId: Long): Long
}
