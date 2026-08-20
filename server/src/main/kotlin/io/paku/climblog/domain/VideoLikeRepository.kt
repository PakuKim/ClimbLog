package io.paku.climblog.domain

interface VideoLikeRepository {
    suspend fun toggleLike(userId: Long, videoId: Long): Boolean
    suspend fun isLiked(userId: Long, videoId: Long): Boolean
    suspend fun getLikeCount(videoId: Long): Long
}
