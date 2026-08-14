package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: Long,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: Long
)
