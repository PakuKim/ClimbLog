package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class SearchVideosUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    operator fun invoke(query: String, pageSize: Int = 10): Flow<PagingData<io.paku.climblog.domain.model.video.Video>> {
        // TODO: Update repository to support search query if needed
        return videoRepository.getVideosPaging(type = "HOME", pageSize = pageSize)
    }
}
