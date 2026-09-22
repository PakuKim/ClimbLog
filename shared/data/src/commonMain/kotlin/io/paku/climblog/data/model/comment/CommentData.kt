package io.paku.climblog.data.model.comment

import kotlinx.datetime.LocalDateTime

data class CommentData(
    val id: Long,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: LocalDateTime
)

data class CommentFeedData(
    val items: List<CommentData>,
    val nextCursor: Long?
)
