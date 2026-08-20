package io.paku.climblog.domain

import io.paku.climblog.domain.model.video.Video

interface VideoRepository {
    suspend fun save(video: Video): Video
    suspend fun findById(id: Long): Video?
    suspend fun findAllByUserId(userId: Long): List<Video>
    suspend fun findAllPaged(cursor: Long?, limit: Int): List<Video>
    suspend fun findRandom(limit: Int): List<Video>
}
