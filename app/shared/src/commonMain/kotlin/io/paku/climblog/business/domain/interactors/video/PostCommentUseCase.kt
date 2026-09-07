package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment

class PostCommentUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long, content: String): Result<Comment> {
        return videoRepository.postComment(videoId, content)
    }
}
