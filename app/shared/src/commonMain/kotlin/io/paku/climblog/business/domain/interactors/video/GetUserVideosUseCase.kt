package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.video.VideoFeed

class GetUserVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(userId: Long): Result<VideoFeed> {
        return videoRepository.getVideos(userId = userId)
    }
}
