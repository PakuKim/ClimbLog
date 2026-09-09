package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.video.Video

class GetRandomVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(limit: Int): Result<List<Video>> {
        return videoRepository.getRandomVideos(limit)
    }
}
