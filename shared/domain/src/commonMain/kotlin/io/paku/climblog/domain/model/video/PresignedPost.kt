package io.paku.climblog.domain.model.video

data class PresignedPost(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)
