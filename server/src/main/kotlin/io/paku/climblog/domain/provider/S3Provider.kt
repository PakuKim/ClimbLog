package io.paku.climblog.domain.provider

import java.net.URL

interface S3Provider {
    /**
     * Legacy Single Upload (PUT)
     */
    fun generatePresignedUploadUrl(
        bucketName: String,
        key: String,
        contentType: String
    ): URL

    /**
     * Direction A: Multipart POST Upload (S3 POST Object)
     * Returns a map of form fields and the target URL.
     */
    fun generatePresignedPost(
        bucketName: String,
        key: String,
        contentType: String
    ): S3PostData
}

data class S3PostData(
    val url: String,
    val fields: Map<String, String>
)
