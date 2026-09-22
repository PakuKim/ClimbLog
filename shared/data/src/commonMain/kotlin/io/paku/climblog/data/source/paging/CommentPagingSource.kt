package io.paku.climblog.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.paku.climblog.data.model.comment.CommentData
import io.paku.climblog.data.model.comment.CommentFeedData

class CommentPagingSource(
    private val fetchComments: suspend (Long?, Int) -> Result<CommentFeedData>
) : PagingSource<Long, CommentData>() {

    override fun getRefreshKey(state: PagingState<Long, CommentData>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, CommentData> {
        val cursor = params.key
        val limit = params.loadSize

        val result = fetchComments(cursor, limit)

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
