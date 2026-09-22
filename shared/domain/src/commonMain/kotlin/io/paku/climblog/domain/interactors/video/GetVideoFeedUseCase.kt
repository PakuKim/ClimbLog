package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import kotlinx.coroutines.flow.Flow

class GetVideoFeedUseCase(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(pageSize: Int = 10): Flow<PagingData<Video>> {
        return videoRepository.getVideosPaging(type = "HOME", pageSize = pageSize)
    }
}
