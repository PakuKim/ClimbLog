package io.paku.climblog.domain.interactors.video

class GetRandomVideosUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    suspend operator fun invoke(limit: Int): Result<io.paku.climblog.domain.model.video.VideoFeed> {
        return videoRepository.getVideos(type = "RANDOM", limit = limit)
    }
}
