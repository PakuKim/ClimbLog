package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.video.VideoFeed

class GetRandomVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(limit: Int): Result<VideoFeed> {
        return videoRepository.getVideos(type = "RANDOM", limit = limit)
    }
}
