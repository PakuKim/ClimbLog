package io.paku.climblog.domain.model.video

data class PresignedPut(
    val uploadUrl: String,
    val objectKey: String
)
