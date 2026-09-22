package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import kotlinx.coroutines.flow.Flow

class GetSingleVideoUseCase(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(videoId: Long, pageSize: Int = 10): Flow<PagingData<Video>> {
        // For now, we can just fetch the feed or implement a specific single video feed
        return videoRepository.getVideosPaging(type = "HOME", pageSize = pageSize)
    }
}
