package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.video.VideoFeed

class GetVideoFeedUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(cursor: Long?, limit: Int): Result<VideoFeed> {
        return videoRepository.getVideos(type = "HOME", cursor = cursor, limit = limit)
    }
}
