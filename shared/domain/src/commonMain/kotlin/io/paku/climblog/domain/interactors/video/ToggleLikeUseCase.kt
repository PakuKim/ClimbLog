package io.paku.climblog.domain.interactors.video

class ToggleLikeUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Result<Boolean> {
        return videoRepository.toggleLike(videoId)
    }
}
