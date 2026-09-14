package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoCommentRepository
import io.paku.climblog.domain.model.video.VideoComment

class GetVideoCommentsUseCase(
    private val videoCommentRepository: VideoCommentRepository
) {
    suspend operator fun invoke(videoId: Long, cursor: Long?, limit: Int): Result<List<VideoComment>> = runCatching {
        videoCommentRepository.findAllByVideoIdPaged(videoId, cursor, limit)
    }
}