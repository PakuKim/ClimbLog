package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.VideoStatus

class UpdateVideoStatusUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long, status: VideoStatus): Result<Boolean> = runCatching {
        videoRepository.updateStatus(videoId, status)
    }
}
