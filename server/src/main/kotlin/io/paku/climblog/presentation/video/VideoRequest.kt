package io.paku.climblog.presentation.video

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(
    val fileName: String,
    val contentType: String
)

@Serializable
data class RegisterVideoRequest(
    val title: String,
    val description: String? = null,
    val s3Key: String,
    val cruxStartTime: Double? = null,
    val cruxEndTime: Double? = null
)
