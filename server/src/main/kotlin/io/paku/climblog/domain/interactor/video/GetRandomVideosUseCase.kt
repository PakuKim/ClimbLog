package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video

class GetRandomVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(limit: Int = 18): Result<List<Video>> = runCatching {
        videoRepository.findRandom(limit)
    }
}
