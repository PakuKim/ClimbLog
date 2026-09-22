package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GetSingleVideoUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    operator fun invoke(videoId: Long, pageSize: Int = 10): Flow<PagingData<io.paku.climblog.domain.model.video.Video>> {
        // For now, we can just fetch the feed or implement a specific single video feed
        return videoRepository.getVideosPaging(type = "HOME", pageSize = pageSize)
    }
}
