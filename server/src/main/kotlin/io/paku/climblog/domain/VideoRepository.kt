package io.paku.climblog.domain

import io.paku.climblog.domain.model.video.Video

interface VideoRepository {
    suspend fun save(video: Video): Video
    suspend fun findById(id: Long): Video?
    suspend fun findAllByUserId(userId: Long): List<Video>
    suspend fun findAllPaged(
        userId: Long? = null,
        cursor: Long? = null,
        limit: Int = 10,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC"
    ): List<Video>

    suspend fun findHomeFeed(
        currentUserId: Long,
        cursor: Long? = null,
        limit: Int = 10
    ): List<Video>

    suspend fun findRandom(limit: Int, excludedIds: List<Long> = emptyList()): List<Video>
}
