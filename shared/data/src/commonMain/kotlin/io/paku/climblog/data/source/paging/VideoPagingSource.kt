package io.paku.climblog.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class VideoPagingSource(
    private val fetchVideos: suspend (Long?, Int) -> Result<io.paku.climblog.data.model.video.VideoFeedData>
) : PagingSource<Long, io.paku.climblog.data.model.video.VideoData>() {

    override fun getRefreshKey(state: PagingState<Long, io.paku.climblog.data.model.video.VideoData>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, io.paku.climblog.data.model.video.VideoData> {
        val cursor = params.key
        val limit = params.loadSize

        val result = fetchVideos(cursor, limit)

        return if (result.isSuccess) {
            val feed = result.getOrThrow()
            LoadResult.Page(
                data = feed.items,
                prevKey = null,
                nextKey = feed.nextCursor
            )
        } else {
            LoadResult.Error(result.exceptionOrNull()!!)
        }
    }
}
