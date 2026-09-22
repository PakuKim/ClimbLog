package io.paku.climblog.data.mapper.comment

import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.comment.CommentData
import io.paku.climblog.domain.model.comment.Comment

internal object CommentDataMapper : BiMapper<CommentData, Comment> {
    override fun mapToRight(from: CommentData): Comment {
        return Comment(
            id = from.id,
            videoId = from.videoId,
            userId = from.userId,
            userName = from.userName,
            userProfilePhotoUrl = from.userProfilePhotoUrl,
            content = from.content,
            createdAt = from.createdAt
        )
    }

    override fun mapToLeft(from: Comment): CommentData {
        return CommentData(
            id = from.id,
            videoId = from.videoId,
            userId = from.userId,
            userName = from.userName,
            userProfilePhotoUrl = from.userProfilePhotoUrl,
            content = from.content,
            createdAt = from.createdAt
        )
    }
}
