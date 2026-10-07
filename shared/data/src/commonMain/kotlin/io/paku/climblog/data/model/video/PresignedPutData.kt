package io.paku.climblog.data.model.video

data class PresignedPutData(
    val uploadUrl: String,
    val objectKey: String
)
