package io.paku.climblog.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class CommentPagingSource(
    private val fetchComments: suspend (Long?, Int) -> Result<io.paku.climblog.data.model.comment.CommentFeedData>
) : PagingSource<Long, io.paku.climblog.data.model.comment.CommentData>() {

    override fun getRefreshKey(state: PagingState<Long, io.paku.climblog.data.model.comment.CommentData>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, io.paku.climblog.data.model.comment.CommentData> {
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
