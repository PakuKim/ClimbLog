package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.video.Video

class GetVideoFeedUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(cursor: Long?, limit: Int): Result<List<Video>> {
        return videoRepository.getFeed(cursor, limit)
    }
}
