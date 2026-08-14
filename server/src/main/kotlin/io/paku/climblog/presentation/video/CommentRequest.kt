package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    val content: String
)
