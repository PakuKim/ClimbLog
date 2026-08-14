package io.paku.climblog.business.remote.dto.response.video

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
    val createdAt: Long
)
