package io.paku.climblog.business.data.source.remote

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
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

    override suspend fun load(params: PagingSourceLoadParams<Long>): PagingSourceLoadResult<Long, Video> {
        val cursor = params.key
        val limit = params.loadSize
        
        val result = videoRepository.getFeed(cursor, limit)
        
        return if (result.isSuccess) {
            val videos = result.getOrThrow()
            val nextKey = if (videos.isEmpty() || videos.size < limit) null else videos.lastOrNull()?.id
            PagingSourceLoadResultPage<Long, Video>(
                data = videos,
                prevKey = null,
                nextKey = nextKey
            ) as PagingSourceLoadResult<Long, Video>
        } else {
            PagingSourceLoadResultError<Long, Video>(result.exceptionOrNull()!!) as PagingSourceLoadResult<Long, Video>
        }
    }
}
