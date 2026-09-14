package io.paku.climblog.business.remote.dto.response.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentFeedResponse(
    @SerialName("items")
    val items: List<CommentResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long?
)
