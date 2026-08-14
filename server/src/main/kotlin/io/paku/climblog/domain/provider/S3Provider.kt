package io.paku.climblog.domain.provider

import java.net.URL

interface S3Provider {
    fun generatePresignedUploadUrl(
        bucketName: String,
        key: String,
        contentType: String
    ): URL
}
