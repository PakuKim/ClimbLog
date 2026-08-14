package io.paku.climblog.domain

import io.paku.climblog.domain.model.Comment

interface CommentRepository {
    suspend fun save(comment: Comment): Comment
    suspend fun findAllByVideoId(videoId: Long): List<Comment>
}
