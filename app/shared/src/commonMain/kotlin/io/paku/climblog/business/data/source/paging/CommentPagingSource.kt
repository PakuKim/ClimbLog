package io.paku.climblog.business.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.paku.climblog.business.domain.model.comment.Comment
import io.paku.climblog.business.domain.model.comment.CommentFeed

class CommentPagingSource(
    private val fetchComments: suspend (Long?, Int) -> Result<CommentFeed>
) : PagingSource<Long, Comment>() {

    override fun getRefreshKey(state: PagingState<Long, Comment>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Comment> {
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