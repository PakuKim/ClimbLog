package io.paku.climblog.business.data.model.video

data class PresignedPostData(
    val url: String,
    val fields: Map<String, String>,
    val objectKey: String
)
