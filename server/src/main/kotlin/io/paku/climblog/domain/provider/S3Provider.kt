package io.paku.climblog.domain.provider

interface S3Provider {
    /**
     * Generates a presigned PUT URL for Cloudflare R2 / S3 upload.
     */
    fun generatePresignedPutUrl(
        bucketName: String,
        key: String,
        contentType: String
    ): String

    /**
     * Uploads a local file to R2 / S3.
     */
    fun uploadFile(
        bucketName: String,
        key: String,
        file: java.io.File,
        contentType: String
    )

    /**
     * Downloads a file from R2 / S3 to local disk.
     */
    fun downloadFile(
        bucketName: String,
        key: String,
        destinationFile: java.io.File
    )

    /**
     * Checks if an object exists in the S3/R2 bucket.
     */
    fun doesObjectExist(bucketName: String, key: String): Boolean
}
