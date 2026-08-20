package io.paku.climblog.domain.model.video

import io.paku.climblog.domain.ext.now
import kotlinx.datetime.LocalDateTime

data class VideoComment(
    val id: Long = 0L,
    val videoId: Long,
    val userId: Long,
    val userName: String,
    val userProfilePhotoUrl: String?,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)