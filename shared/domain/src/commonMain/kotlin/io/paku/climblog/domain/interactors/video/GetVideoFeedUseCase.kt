package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GetVideoFeedUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    operator fun invoke(pageSize: Int = 10): Flow<PagingData<io.paku.climblog.domain.model.video.Video>> {
        return videoRepository.getVideosPaging(type = "HOME", pageSize = pageSize)
    }
}
