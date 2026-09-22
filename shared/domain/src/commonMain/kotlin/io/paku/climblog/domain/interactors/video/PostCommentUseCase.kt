package io.paku.climblog.domain.interactors.video

class PostCommentUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    suspend operator fun invoke(videoId: Long, content: String): Result<io.paku.climblog.domain.model.comment.Comment> {
        return videoRepository.postComment(videoId, content)
    }
}
