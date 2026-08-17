package io.paku.climblog.business.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Video

class VideoPagingSource(
    private val videoRepository: VideoRepository
) : PagingSource<Long, Video>() {

    override fun getRefreshKey(state: PagingState<Long, Video>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Video> {
        val cursor = params.key
        val limit = params.loadSize
        
        val result = videoRepository.getFeed(cursor, limit)
        
        return if (result.isSuccess) {
            val videos = result.getOrThrow()
            val nextKey = if (videos.isEmpty() || videos.size < limit) null else videos.lastOrNull()?.id
            LoadResult.Page(
                data = videos,
                prevKey = null,
                nextKey = nextKey
            )
        } else {
            LoadResult.Error(result.exceptionOrNull()!!)
        }
    }
}
