package io.paku.climblog.business.domain.model.video

import kotlinx.serialization.Serializable

@Serializable
data class PresignedPostResponse(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)

@Serializable
data class PresignedPostRequest(
    val fileName: String,
    val contentType: String
)
