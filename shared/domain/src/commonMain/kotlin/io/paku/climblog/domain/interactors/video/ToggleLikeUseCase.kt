package io.paku.climblog.domain.interactors.video

import io.paku.climblog.domain.VideoRepository

class ToggleLikeUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Result<Boolean> {
        return videoRepository.toggleLike(videoId)
    }
}
