package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository

class ToggleLikeUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Result<Boolean> {
        return videoRepository.toggleLike(videoId)
    }
}
