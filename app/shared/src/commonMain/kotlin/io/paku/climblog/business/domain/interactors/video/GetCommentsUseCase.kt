package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment

class GetCommentsUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Result<List<Comment>> {
        return videoRepository.getComments(videoId)
    }
}
