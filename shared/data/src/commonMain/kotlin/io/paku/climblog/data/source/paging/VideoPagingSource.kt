package io.paku.climblog.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.paku.climblog.data.model.video.VideoData
import io.paku.climblog.data.model.video.VideoFeedData

class VideoPagingSource(
    private val fetchVideos: suspend (Long?, Int) -> Result<VideoFeedData>
) : PagingSource<Long, VideoData>() {

    override fun getRefreshKey(state: PagingState<Long, VideoData>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, VideoData> {
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
