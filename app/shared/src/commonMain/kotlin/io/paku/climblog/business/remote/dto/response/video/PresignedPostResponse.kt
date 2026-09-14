package io.paku.climblog.business.remote.dto.response.video

import kotlinx.serialization.Serializable

@Serializable
data class PresignedPostResponse(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)