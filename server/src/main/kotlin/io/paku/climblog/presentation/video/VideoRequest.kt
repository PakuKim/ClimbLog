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
    val description: String,
    val s3Key: String,
    val cruxes: List<Crux>,
) {
    @Serializable
    data class Crux(
        val startTime: Double,
        val endTime: Double
    )
}
