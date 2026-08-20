package io.paku.climblog.domain

import io.paku.climblog.domain.model.video.VideoComment

interface VideoCommentRepository {
    suspend fun save(videoComment: VideoComment): VideoComment
    suspend fun findAllByVideoId(videoId: Long): List<VideoComment>
}
