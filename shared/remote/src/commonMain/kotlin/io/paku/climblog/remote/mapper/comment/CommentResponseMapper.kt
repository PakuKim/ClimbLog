package io.paku.climblog.remote.mapper.comment

import io.paku.climblog.contract.comment.CommentResponse
import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.comment.CommentData

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
