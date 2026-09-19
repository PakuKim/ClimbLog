package io.paku.climblog.business.remote.mapper.comment

import io.paku.climblog.business.data.model.comment.CommentData
import io.paku.climblog.business.remote.model.comment.CommentResponse
import io.paku.climblog.core.BiMapper

internal object CommentResponseMapper : BiMapper<CommentResponse, CommentData> {
    override fun mapToRight(from: CommentResponse): CommentData {
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

    override fun mapToLeft(from: CommentData): CommentResponse {
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
