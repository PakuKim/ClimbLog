package io.paku.climblog.remote.mapper.comment

import io.paku.climblog.contract.comment.CommentResponse
import io.paku.climblog.core.BiMapper

internal object CommentResponseMapper : BiMapper<CommentResponse, io.paku.climblog.data.model.comment.CommentData> {
    override fun mapToRight(from: CommentResponse): io.paku.climblog.data.model.comment.CommentData {
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

    override fun mapToLeft(from: io.paku.climblog.data.model.comment.CommentData): CommentResponse {
        return CommentResponse(
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
