package io.paku.climblog.business.domain.interactors.video

import androidx.paging.PagingData
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.comment.Comment
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(videoId: Long, pageSize: Int = 20): Flow<PagingData<Comment>> {
        return videoRepository.getCommentsPaging(videoId, pageSize)
    }
}
