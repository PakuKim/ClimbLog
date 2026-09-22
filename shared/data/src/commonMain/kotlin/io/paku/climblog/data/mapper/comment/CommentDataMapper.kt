package io.paku.climblog.data.mapper.comment

import io.paku.climblog.core.BiMapper

internal object CommentDataMapper : BiMapper<io.paku.climblog.data.model.comment.CommentData, io.paku.climblog.domain.model.comment.Comment> {
    override fun mapToRight(from: io.paku.climblog.data.model.comment.CommentData): io.paku.climblog.domain.model.comment.Comment {
        return _root_ide_package_.io.paku.climblog.domain.model.comment.Comment(
            id = from.id,
            videoId = from.videoId,
            userId = from.userId,
            userName = from.userName,
            userProfilePhotoUrl = from.userProfilePhotoUrl,
            content = from.content,
            createdAt = from.createdAt
        )
    }

    override fun mapToLeft(from: io.paku.climblog.domain.model.comment.Comment): io.paku.climblog.data.model.comment.CommentData {
        return _root_ide_package_.io.paku.climblog.data.model.comment.CommentData(
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
