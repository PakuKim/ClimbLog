package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import kotlinx.coroutines.flow.Flow

class GetUserVideosUseCase(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(
        userId: Long,
        pageSize: Int = 10
    ): Flow<PagingData<Video>> {
        return videoRepository.getVideosPaging(userId = userId, pageSize = pageSize)
    }
}
