package io.paku.climblog.domain.model.comment

import kotlinx.datetime.LocalDateTime

data class Comment(
    val id: Long,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: LocalDateTime
)
