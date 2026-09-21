package io.paku.climblog.contract.comment

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("videoId")
    val videoId: Long,
    @SerialName("userId")
    val userId: Long,
    @SerialName("userName")
    val userName: String,
    @SerialName("userProfilePhotoUrl")
    val userProfilePhotoUrl: String?,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
)

@Serializable
data class CommentFeedResponse(
    @SerialName("items")
    val items: List<CommentResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long?
)

@Serializable
data class CommentRequest(
    @SerialName("content")
    val content: String
)
