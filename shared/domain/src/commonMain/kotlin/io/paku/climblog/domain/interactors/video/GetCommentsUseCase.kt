package io.paku.climblog.domain.interactors.video

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository
) {
    operator fun invoke(videoId: Long, pageSize: Int = 20): Flow<PagingData<io.paku.climblog.domain.model.comment.Comment>> {
        return videoRepository.getCommentsPaging(videoId, pageSize)
    }
}
