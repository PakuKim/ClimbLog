package io.paku.climblog.business.domain.model.comment

data class CommentFeed(
    val items: List<Comment>,
    val nextCursor: Long?
)