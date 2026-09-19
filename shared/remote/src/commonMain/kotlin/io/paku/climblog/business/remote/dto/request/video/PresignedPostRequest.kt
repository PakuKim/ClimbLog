package io.paku.climblog.business.remote.dto.request.video

import kotlinx.serialization.Serializable

@Serializable
data class PresignedPostRequest(
    val fileName: String,
    val contentType: String
)
